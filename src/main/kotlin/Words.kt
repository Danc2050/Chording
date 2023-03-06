
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

// {sorted chords strung together as string : Pair(Original word, Chord + Character entry list) }
val sortPiecesTest = mutableMapOf<String, Pair<String, List<String>>>()
var duplicateCount = 0L


object Main {
    fun findSingleFix(word: String, fixDictionary: MutableSet<String>, sortPieces: Boolean? = false) {
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
        fun createStringUsingRootChordingMethod(word: String, substrings: List<String>, sortPieces: Boolean? = false): String {
            // create a mutable list to store the selected substrings
            var selectedSubstrings = mutableListOf<String>()

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

            if (sortPieces == true) {
                selectedSubstrings = selectedSubstrings.map { it.toCharArray().sortedWith(compareBy {it}).joinToString("") }.toMutableList()
                //selectedSubstrings.forEachIndexed { index, element -> selectedSubstrings[index] = element.toCharArray().sorted().joinToString("") }
            }

            // return the selected substrings joined by '+'
            //return selectedSubstrings.joinToString("+")
            return selectedSubstrings.joinToString(" ")
        }

        fun splitByThreeOrTwoTheory(word: String): String {
            if (word.length < 2) {
                return word
            } else {
                val splitSize = if(word.length >= 3) 3 else 2
                val split = word.chunked(splitSize)
                val sortedSplit = split.map { it.toList().sorted().joinToString("") }
                return sortedSplit.joinToString("")
            }
        }


        //println("Roots for $word are: $roots")
        //val output = createStringUsingRootChordingMethod(word, roots, sortPieces)

        val output = splitByThreeOrTwoTheory(word)
        zipChordOutputFile += "$output\t $word\n"
        if (sortPieces == true) {
            // take spaces out to compare string... is it's munged form identical to that of another ones munged form?
            if (sortPiecesTest.contains(output.split(" ").joinToString(""))) {
                println("DUPLICATE EXISTS for word: $word. Is already in our dictionary as: ${sortPiecesTest[(output.split(" ").joinToString(""))]}")
                duplicateCount++
            } else {
                sortPiecesTest[output.split(" ").joinToString("")] = Pair(word, output.split(" "))
            }
        }
        //println("output: $output")
        val numberOfChords = output
            .split("+")
            .count { it.length > 1 }
            .toFloat()
        var totalUnits = output
            .split("+")
            .size.toFloat()
        //println("Percent chorded: ${numberOfChords.div(totalUnits).times(100)}")

        totalPercentChordableInAllWords += numberOfChords
        totalUnitsInAllWords += totalUnits
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

    fun rootAnagramTest(csvIterator: MutableIterator<Any?>) {
        /* Here's a theory: if I chord + character entry a word and sort each entry as I go (chord...character...),
            is the resulting string unique? Can I then map it to the word?

            Take `aerospace` for example.
            If I decide the chord + character entry is: [`aer`, `o`, `space`] the sorted version is [`aer`, `o`, `aceps`]
            and the final word is 'aeroaceps', which is not a word.

            The sum result means I can mash letters on my keyboard to form the word and get the word...
            One could take this even further perhaps.. if I mash the keys in chords in any order, would I ever get a collision for
             the word aerospace? Or is it truly unique? How many words are like this? Most 5 letter words?
         */

        // USING CHARACHORDER'S DICTIONARY HERE
        val wordDictionary = mutableSetOf<String>()
        val prefixDictionary = mutableSetOf<String>()
        while (csvIterator.hasNext()) {
            val ccBuilderFile: CharaChorderBean = csvIterator.next() as CharaChorderBean
            ccBuilderFile.output?.let { wordDictionary.add(it) }
        }
        //Files.newBufferedReader(Paths.get("src\\main\\resources\\roots")).use { reader ->
        Files.newBufferedReader(Paths.get("src\\main\\resources\\frequent")).use { reader ->
            //Files.newBufferedReader(Paths.get("src\\main\\resources\\prefix")).use { reader ->
            for (line in reader.lines()) {
                //println(line)
                prefixDictionary.add(line)
            }
        }

        // WIKIPEDIA ALL WORDS
          // NOTE: this function takes very long to run if you are using it for yourself
//        Files.newBufferedReader(Paths.get("src\\main\\resources\\WikiDictionary.txt")).use { reader ->
//            for (line in reader.lines()) {
//                //println(line)
//                wordDictionary.add(line)
//            }
//        }
//
//        Files.newBufferedReader(Paths.get("src\\main\\resources\\wikipediaroots")).use { reader ->
//            for (line in reader.lines()) {
//                //println(line)
//                prefixDictionary.add(line)
//            }
//        }


        for (word in wordDictionary) {
            findSingleFix(word, prefixDictionary, sortPieces = true)
        }

        println(sortPiecesTest)
        println("DONE")
    }

    /* Function finds all combinations of a word, without any special note as to roots or anything.
        So a word like `the` can be typed in a chord the following ways: [['ht', 'e'], ['t', 'eh']].
        So here is the algorithm when a user attempts to type the word `the`:
         - `th`: it will be sorted and output `ht`
         - `e`, it will be sorted or sorting is skipped since it's one letter long
         - final output when sorted would be `hte`, which would then be an entry into a dictionary that would map to `the`
     */
    fun combinationsRegardlessOfRoot(csvIterator: MutableIterator<Any?>): Boolean {
        val wordDictionary = mutableSetOf<String>()
//        Files.newBufferedReader(Paths.get("src\\main\\resources\\WikiDictionary.txt")).use { reader ->
//            for (line in reader.lines()) {
//                //println(line)
//                wordDictionary.add(line)
//            }
//        }

        val minChordLength = 2
        val maxChordLength = 4

        while (csvIterator.hasNext()) {
            val ccBuilderFile: CharaChorderBean = csvIterator.next() as CharaChorderBean
            ccBuilderFile.output?.let { word ->
                if (word.length <= 4) {
                    print("$word ")
                }
                wordDictionary.add(word)
            }
        }
        println()

        val setOfWords = mutableSetOf<String>()

        val wordsThatConflict = mutableSetOf<String>()
        var conflicts = 0
//        val accomodation = File("src/main/kotlin/accommodation.txt")

        File("src/main/kotlin/chords.txt").printWriter().use { writer ->
            for (word in wordDictionary) {
                //val res = sortedCombinations(word)
                val res = findUniqueStringsFromCombiningChunkedChords(word, minChordLength, maxChordLength)
//                if (word == "accommodation") {
//                    accomodation.writeText(res.joinToString(","))
//                }
                if (word == "teen") {
                    println(res)
                }


                for (permutedString in res) {
                    if (permutedString in setOfWords) {
                        println("$permutedString is already in dictionary for word $word!!!")
                        conflicts++
                        wordsThatConflict.add(word)
                    } else {
                        // normally I'd say don't add the permutation that is the string itself
                        //  but we could chord that string at once (sometimes you get lucky and hit the letters in order
                        //  so we add it here anyway. It's also useful for autospacing in Zipchord
                        setOfWords.add(permutedString)
                        writer.println("$permutedString\t$word")
                    }
                }
                //println("$word to ${res}")
            }
        }

        println("this many conflicts: $conflicts")
        println("this many root words: $wordsThatConflict")

        return true
    }


    fun prefixFinder() {
        val SAMPLE_CSV_FILE_PATH =
//            "src\\main\\resources\\WikiDictionary.txt"
            "src\\main\\resources\\CharaChorder Builder (BETA) - Daniel Compound Chording edited (CC1).csv"
//        "src\\main\\resources\\CharaChorder Builder (BETA) - Aphit (CC1).csv"

        Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH)).use { reader ->
            val csvToBean: CsvToBean<Any?>? = CsvToBeanBuilder<Any?>(reader)
                .withType(CharaChorderBean::class.java)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
            val csvIterator: MutableIterator<Any?> = csvToBean!!.iterator()
            // shorthandExperiment(csvIterator)
            //autoChentryMaker(csvIterator)
            combinationsRegardlessOfRoot(csvIterator)
            //rootAnagramTest(csvIterator)

        }



        println(howManyCanBeTypedGreaterThan50WithASinglePrefix)
        println(uniqueRootsToDoAbove.size)
        println("% of all chords in all words: ${totalPercentChordableInAllWords.div(totalUnitsInAllWords).times(100)}")
    }

    /** Here is my thinking: if we have a sequence of input BEFORE a (white)space, then we can sort that input to a chord.
     * Is the chord unique? Let's find out!
     * */
    fun sortedWordTest() {
    }

    fun findImpossibleChords() {
        /** Finds impossible chords for CC1 (NOTE: CCL does not have this prohibition)
         *
         */

//        TODO("For each word, iterate through all fingers and make sure that the (inputSet - finger[index]).size >= 3." +
//                "If not, you can be uber specific and declare what is wrong.")

        //val SAMPLE_CSV_FILE_PATH = "src\\main\\resources\\CharaChorder Builder (BETA) - Aphit (CC1).csv"
        val SAMPLE_CSV_FILE_PATH = "src\\main\\resources\\CharaChorder Builder (BETA) - Daniel Compound Chording (CC1).csv"
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

    /*
     Essentially we want all permutations of a string that one could chord. This makes more real the thought of typing
     in words, by typing in chunks of those words.
      Notes:
        - We don't want to match on all permutations of a string, only the chordable ones (n=4 in this example)
        - So for example we take the first 4 letters of a word (typing 4 letters at a time) and we realize those letters can be typed in any order
            - Foe example for the word `about` never would someone type 'auobt' (in any chorded or character entry combination) as they would type only the first 4 letters,
                'abou'` and may mash them in any order ('auob', 'boau', etc.) + the remaining string 't'
        - The goal would be at the end of a user chording/character "entrying" this string AND pressing space, they would then
          have a unique string and then some program would take that unique string and know that a chorded + character entry COULD
          match to the word (e.g., `about`). This program generates that list of unique strings
     */
    fun findUniqueStringsFromCombiningChunkedChords(word: String, minChordLength: Int, maxChordLength: Int): MutableSet<String> {
        // Amount we'd like to chunk our word by...can be configurable later, but we do a max of 4 letters at a time or the length of the word if < 5
        var n = if (word.length <= maxChordLength) word.length else maxChordLength
        val variationsOfChunks = mutableListOf<String>()
        val variationStringsFromCombiningChunks = mutableSetOf<String>()

        fun combineLists(lists: List<List<String>>): List<String> {
            if (lists.isEmpty()) {
                return emptyList()
            }

            val result = mutableListOf<String>()

            fun combineHelper(currentIndex: Int, currentString: String) {
                if (currentIndex == lists.size) {
                    result.add(currentString)
                    return
                }

                for (element in lists[currentIndex]) {
                    combineHelper(currentIndex + 1, currentString + element)
                }
            }
            combineHelper(0, "")
            return result
        }

        for (i in n downTo minChordLength) {
            //println("PERMUTATION ${abs(n - i)}")
            val chunks = word.chunked(i)
            // in this case, we just permute all the letters as they all can be pressed in one go, without combining them with other presses
            if (chunks.size == 1) {
                variationStringsFromCombiningChunks.addAll(chunks[0].permute())
            } else {
                val outputs = combineLists(chunks.map { it.permutations().toList() })
                variationStringsFromCombiningChunks.addAll(outputs)
            }
        }
//        println("VARIATIONS OF CHUNKS")
//        println(variationStringsFromCombiningChunks)

        //println("REVERSED")
        // chunk backwards (takes care of odd length words)
        for (i in n downTo minChordLength) {
            val chunks = word.reversed().chunked(i).reversed()
            // in this case, we just permute all the letters as they all can be pressed in one go, without combining them with other presses
            if (chunks.size == 1) {
                variationStringsFromCombiningChunks.addAll(chunks[0].permute())
            } else {
                val outputs = combineLists(chunks.map { it.permutations().toList() })
                variationStringsFromCombiningChunks.addAll(outputs)
            }
        }
        return variationStringsFromCombiningChunks

//        println("VARIATIONS OF CHUNKS")
//        println(variationStringsFromCombiningChunks)
        //println("All variations of about")
        //println("about".permute())
    }

    fun String.permute(result: String = ""): List<String> =
        if (isEmpty()) listOf(result) else flatMapIndexed { i, c -> removeRange(i, i + 1).permute(result + c) }

    fun String.permutations(): List<String> {
        if (length == 1) {
            return listOf(this)
        }
        val permutations = mutableSetOf<String>()
        for (i in indices) {
            val c = this[i]
            val remaining = substring(0, i) + substring(i + 1)
            for (perm in remaining.permutations()) {
                permutations.add(c + perm)
            }
        }
        return permutations.toList()
    }
    @JvmStatic
    fun main(args: Array<String>) {
        prefixFinder()
        //println(findUniqueStringsFromCombiningChunkedChords("about", 4))
//        println(smallTest("the", 4))
        //println("about".permute())

        //findImpossibleChords()
    }
}