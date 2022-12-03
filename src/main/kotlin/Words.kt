import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import org.jsoup.Jsoup
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

/**
 * This program search for a prefix in a group of words in an optimal way
 */
object Main {
    private fun findValues(words: MutableSet<String>, prefix: String): List<String> {
        //Transform list to map ignoring repeated words
        val map: Map<String, String> = words.stream().collect(Collectors.toMap(
            { obj: String -> obj }, (Function { `val`: String -> `val` }),
            { `val`: String, val2: String ->
                println("Value: $`val`, $val2 repeated")
                `val`
            })
        )

        //Transform map to SortedMap to do searching operations
        val dictionary: SortedMap<String, String> = TreeMap(map)
        //println("Dictionary: $dictionary")

        //Search for prefix
        val subMap: SortedMap<*, *> = dictionary.subMap(prefix, prefix + Character.MAX_VALUE)

        //Transform into a collection
        val values: List<String> = subMap.values.stream().collect(Collectors.toList()) as List<String>
        return values
    }
    fun findSingleFix(words: MutableSet<String>, fix: String) {
        for (word in words) {
            var start = 0
            do {
                val index = word.indexOf(fix, start)
                start = index+1
                if (index == 0) {
                    println("Word: $word, fix $fix")
                    println("Prefix")
                } else if (index == word.length - fix.length) {
                    println("Word: $word, fix $fix")
                    println("Suffix")
                } else {
                    println("Word: $word, fix $fix")
                    println("Infix")
                }
            } while (index != -1)
        }
    }

    fun prefixFinder() {
        val SAMPLE_CSV_FILE_PATH =
            "src\\main\\resources\\CharaChorder Builder (BETA) - Daniel Compound Chording (CC1).csv"
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
                //println(ccBuilderFile.output)
                ccBuilderFile.output?.let { dictionary.add(it) }
                //println("==========================")
            }
        }
        println(dictionary)


        // Read prefix for now (TODO -- infix, suffix)
        val prefixDictionary = mutableSetOf<String>()
        Files.newBufferedReader(Paths.get("src\\main\\resources\\prefix")).use { reader ->
            for (line in reader.lines()) {
                //println(line)
                prefixDictionary.add(line)
            }
        }



        for (prefix in prefixDictionary) {
            //val prefix = "de"
//        val words = mutableSetOf("dog", "deer", "death", "death", "deal", "apple")
//        println("Values: " + findValues(words, prefix))
            var values = findValues(dictionary, prefix)
            if(prefix == "one") {
                findSingleFix(dictionary, prefix)
            } else if (prefix.length > 3 && values.isNotEmpty()) {
                /*println(prefix)
                println("Values: $values")*/
            }
        }
        TODO(
            "'root out' the prefix, infix, and/or suffix and add it to a new file that specifies input and finger " +
                    "you would use to compound chord that chord + any character entry"
        )

    }

    fun findImpossibleChords() {
        /** Finds impossible chords for CC1 (NOTE: CCL does not have this prohibition)
         *
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

//        TODO("For each word, iterate through all fingers and make sure that the (inputSet - finger[index]).size >= 3." +
//                "If not, you can be uber specific and declare what is wrong.")

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

    fun parseHtmlForRoots() {
        // https://membean.com/roots/
        // could just call the html directly with Jsoup, but later...
        var HTML = File("src/main/resources/html").bufferedReader().readLines().joinToString()
        val membeanRoots = Jsoup.parse(HTML)
        val section = membeanRoots.selectXpath("/html/body/div[1]/main/section[2]/div")
        val rootSet = mutableSetOf<String>()
        for (element in section) {
            var children = element.childNodes()
            for (child in children) {
                if (child.childNodes().size > 2) {
                    val root = child.childNode(1)
                    val string = root.childNode(1).childNode(0).toString().trimStart('-').trimEnd('-')
                    if (
                        string.length > 1 // no "-{letter}", "-{letter}-", "{letter}-" roots
                       )
                    {

                    // TODO("for now, let's just call prefix, suffix, and infix "roots" and not distinguish among them")
                    // We use a set because we may have a situation like `-al` `-al-`, `al-` which all produce `al` as a root
                    rootSet.add(root.childNode(1).childNode(0).toString().trimStart('-').trimEnd('-'))
                    }
                    // TODO("Can add definitions in text file also, but later...")
                    //println(root.childNode(3).childNode(0).toString())
                }
            }
        }
        File("src/main/resources/roots").writeText(java.lang.String.join("\n", rootSet))
    }

    @JvmStatic
    fun main(args: Array<String>) {
        //findImpossibleChords()
        parseHtmlForRoots()
    }
}