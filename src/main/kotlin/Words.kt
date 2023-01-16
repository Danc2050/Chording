import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import java.io.File
import java.lang.Math.abs
import java.nio.file.Files
import java.nio.file.Paths

/**
 * This program search for a prefix in a group of words in an optimal way
 */

val L_pinky = setOf("numshift", "ambithrow", "shift", "alt")
val L_ring = setOf("ctrl", ",", ".", "u")
val L_middle= setOf("del", "-", "o", "i")
val L_index= setOf("bs", "space", "e", "r")
val LT1= setOf("v", "m", "c", "k")
val LT2= setOf("click", "g", "z", "w")
val RT1= setOf("p", "f", "d", "h")
val RT2= setOf("x", "b", "q", "dup")
val R_index= setOf("enter", "a", "t", "space")
val R_middle= setOf("tab", "l", "n", "j")
val R_ring= setOf("ctrl", "y", "s", ";")
val R_pinky = setOf("alt", "shift", "ambithrow", "numshift")
val fingers = setOf(L_pinky, L_ring, L_middle, L_index, LT1, LT2, RT1, RT2, R_index, R_middle, R_ring, R_pinky)

// should consider all prefixes maybe (in addition to or substitute to)
var howManyCanBeTypedGreaterThan50WithASinglePrefix = 0
var uniqueRootsToDoAbove = mutableSetOf<String>()

// Percent chorded per root
var totalPercentChordableInAllWords = 0f
var totalUnitsInAllWords = 0f


var zipChordOutputFile = ""

object Main {
    fun findSingleFix(word: String, fixDictionary: MutableSet<String>) {
        /* For now, keep track of all of our roots in a word...later decide how to build the whole word via a chord and use
            roots of certain length (e.g., max 5).
         */
        var wordCombinations = mutableSetOf<String>()
        var tmpWord = word
        while (tmpWord.isNotEmpty()) {
            wordCombinations += tmpWord.windowed(tmpWord.length, 1, true).filter{it.length > 1}.toSet()
            wordCombinations += tmpWord.reversed().windowed(tmpWord.length, 1, true).map{it.reversed()}.filter{it.length > 1}.toSet()
            tmpWord = tmpWord.substring(1)
        }

        var roots = wordCombinations.filter{fixDictionary.contains(it)}

        // I think there is a bug here...for things like "international" the math doesn't add up
        if (roots.any { it.length >= (word.length * .50) }) {
            howManyCanBeTypedGreaterThan50WithASinglePrefix++
            uniqueRootsToDoAbove.add(roots.maxBy { it.length})
        }

        /* TODO -- you must keep track of what words have duplicate letters.. If the duplicate letter length is only == 1,
           then you can simply add 'dup' to the output. But you must also keep a map of words so you don't have collisions
           The below is an example of a colission because `dup` is chorded at the same time)
           var mapOfChordedRoots = mutableMapOf("press" to "pres+dup", "prees: to "pre+dup+s")
           The below is an example of a check for a case where we would like to introduce `dup`.
           if (abs(line.toSet().size - line.length) == 1) {
         */
        fun createString(word: String, substrings: List<String>): String {
            // create a mutable list to store the selected substrings
            val selectedSubstrings = mutableListOf<String>()

            // create a variable to store the current index in the word
            var currentIndex = 0

            // create a variable to store the end index of the word
            val endIndex = word.length - 1

            // iterate through the substrings
            while (currentIndex <= endIndex) {
                // create a list of substrings that start at the current index
                val substringsStartingAtCurrentIndex = substrings.filter { word.indexOf(it, currentIndex) == currentIndex }

                // sort the substrings by length in descending order
                val sortedSubstrings = substringsStartingAtCurrentIndex.sortedByDescending { it.length }

                // check if there are any substrings that start at the current index
                if (sortedSubstrings.isNotEmpty()) {
                    // add the longest substring to the list of selected substrings
                    selectedSubstrings.add(sortedSubstrings.first())

                    // update the current index to the end of the substring
                    currentIndex += sortedSubstrings.first().length
                } else {
                    // if there are no substrings that start at the current index, add the individual letters as substrings
                    selectedSubstrings.add(word[currentIndex].toString())
                    currentIndex += 1
                }
            }
            // return the selected substrings joined by '+'
            //return selectedSubstrings.joinToString("+")
            return selectedSubstrings.joinToString(" ")
        }

//        if (word == "pressure") {
            println("Roots for $word are: $roots")
            val output = createString(word, roots)
            zipChordOutputFile += "$output\t $word\n"
            println("output: $output")
            val numberOfChords = output
                .split("+")
                .count { it.length > 1 }
                .toFloat()
            var totalUnits = output
                .split("+")
                .size.toFloat()
            println("Percent chorded: ${numberOfChords.div(totalUnits).times(100)}")

            totalPercentChordableInAllWords += numberOfChords
            totalUnitsInAllWords += totalUnits
//        }
    }


    /**
        Determines if we could have fewer collisions with shorthand magic...
        Answer is: Yes, but they don't always make sense
     */
    fun shorthandExperiment(csvIterator: MutableIterator<Any?>) {
        val setOfChords = mutableSetOf<String>()
        val wordDictionary = mutableSetOf<String>()
        while (csvIterator.hasNext()) {
            val ccBuilderFile: CharaChorderBean = csvIterator.next() as CharaChorderBean
            //println(ccBuilderFile.output)
            //println(ccBuilderFile.output)
            ccBuilderFile.output?.let { wordDictionary.add(it) }
            // Trying a t-line shorthand thing here...
            fun remVowel(str: String): Pair<String, String> {
                var noVowels = str.replace("[aeiouAEIOU]".toRegex(), "")
                var noDuplicateConsonantsOrVowels = noVowels.toSet().joinToString("")
                return Pair(noVowels, noDuplicateConsonantsOrVowels)
            }
            var tLineChord = remVowel(ccBuilderFile.output.toString())
            if (!setOfChords.contains(tLineChord.first)) {
                setOfChords.add(tLineChord.first)
            } else if (!setOfChords.contains(tLineChord.second)) {
                setOfChords.add(tLineChord.second)
            } else {
                println("DUPLICATE for word ${ccBuilderFile.output}: $tLineChord")
            }
            //println(remVowel(ccBuilderFile.output.toString()))

            //println("==========================")
        }
        println(wordDictionary)
    }

    fun seeingHowManyLettersHaveDuplicates() {
        var moreThanOneLetter = 0
        // Read prefix for now (TODO -- infix, suffix)
        Files.newBufferedReader(Paths.get("src\\main\\resources\\wikipediaroots")).use { reader ->
            for (line in reader.lines()) {
                if (line.toSet().size < line.length) {
                    moreThanOneLetter++
                }
                // needing to add dup here..
                if (abs(line.toSet().size - line.length) > 1) {
                    //println(line)
                    //moreThanOneLetter++
                }
            }
        }
        println(moreThanOneLetter)
    }

    /**
     * Adds all the
     */
    fun autoChentryMaker(csvIterator: MutableIterator<Any?>) {
        val wordDictionary = mutableSetOf<String>()
        val prefixDictionary = mutableSetOf<String>()
        while (csvIterator.hasNext()) {
            val ccBuilderFile: CharaChorderBean = csvIterator.next() as CharaChorderBean
            ccBuilderFile.output?.let { wordDictionary.add(it) }
        }
        // Read prefix for now (TODO -- infix, suffix)
        //Files.newBufferedReader(Paths.get("src\\main\\resources\\roots")).use { reader ->
        Files.newBufferedReader(Paths.get("src\\main\\resources\\wikipediaroots")).use { reader ->
            //Files.newBufferedReader(Paths.get("src\\main\\resources\\prefix")).use { reader ->
            for (line in reader.lines()) {
                //println(line)
                prefixDictionary.add(line)
            }
        }

        for (word in wordDictionary) {
            findSingleFix(word, prefixDictionary)
        }
        File("src/main/kotlin/ZipChordDictionary.txt").writeText(zipChordOutputFile)
    }

    fun prefixFinder() {
        val SAMPLE_CSV_FILE_PATH =
            "src\\main\\resources\\CharaChorder Builder (BETA) - Daniel Compound Chording (CC1).csv"
        Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH)).use { reader ->
            val csvToBean: CsvToBean<Any?>? = CsvToBeanBuilder<Any?>(reader)
                .withType(CharaChorderBean::class.java)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
            val csvIterator: MutableIterator<Any?> = csvToBean!!.iterator()
            // shorthandExperiment(csvIterator)
            autoChentryMaker(csvIterator)
        }



        println(howManyCanBeTypedGreaterThan50WithASinglePrefix)
        println(uniqueRootsToDoAbove.size)
        println("% of all chords in all words: ${totalPercentChordableInAllWords.div(totalUnitsInAllWords).times(100)}")

        TODO(
            "'root out' the prefix, infix, and/or suffix and add it to a new file that specifies input and finger " +
                    "you would use to compound chord that chord + any character entry"
        )
    }

    fun findImpossibleChords() {
        /** Finds impossible chords for CC1 (NOTE: CCL does not have this prohibition)
         *
         */

//        TODO("For each word, iterate through all fingers and make sure that the (inputSet - finger[index]).size >= 3." +
//                "If not, you can be uber specific and declare what is wrong.")

        //val SAMPLE_CSV_FILE_PATH = "src\\main\\resources\\CharaChorder Builder (BETA) - Aphit (CC1).csv"
        val SAMPLE_CSV_FILE_PATH = "src\\main\\resources\\CharaChorder Builder (BETA) - Aphit (CC1).csv"
        //val SAMPLE_CSV_FILE_PATH = "src\\main\\resources\\CharaChorder Builder (BETA) - Hauntie (CC1).csv"

        // TODO -- read in dictionary or list of words
        val dictionary = mutableSetOf<String>()
        Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH)).use { reader ->
            val csvToBean: CsvToBean<Any?>? = CsvToBeanBuilder<Any?>(reader)
                .withType(CharaChorderBean::class.java)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
            val csvIterator: MutableIterator<Any?> = csvToBean!!.iterator()
            while (csvIterator.hasNext()) {
                val ccBuilderFile: CharaChorderBean = csvIterator.next() as CharaChorderBean
                if (ccBuilderFile.input?.length!! > 0) {
                    val inputSet = ccBuilderFile.input!!.split("+")
                    println(inputSet)
                    for (finger in fingers) {
                        var a = (inputSet - finger)
                        if (kotlin.math.abs(inputSet.size - a.size) > 1) {
                            println(a.size)
                        }
                    }
                    //println(ccBuilderFile.input)

                }
                //ccBuilderFile.output?.let { dictionary.add(it) }
                //println("==========================")
            }
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        prefixFinder()
        //findImpossibleChords()
    }
}