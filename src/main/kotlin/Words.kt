import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.function.Function
import java.util.stream.Collectors

/**
 * This program search for a prefix in a group of words in an optimal way
 */
object Main {
    private fun findValues(words: List<String>, prefix: String): List<String> {
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
        println("Dictionary: $dictionary")

        //Search for prefix
        val subMap: SortedMap<*, *> = dictionary.subMap(prefix, prefix + Character.MAX_VALUE)

        //Transform into a collection
        val values: List<String> = subMap.values.stream().collect(Collectors.toList()) as List<String>
        return values
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val SAMPLE_CSV_FILE_PATH = "C:\\Users\\conne\\IdeaProjects\\CharaChorderPrefix\\src\\main\\resources\\CharaChorder Builder (BETA) - Daniel Compound Chording (CC1).csv"
        // TODO -- read in dictionary or list of words
        Files.newBufferedReader(Paths.get(SAMPLE_CSV_FILE_PATH)).use { reader ->
            val csvToBean: CsvToBean<Any?>? = CsvToBeanBuilder<Any?>(reader)
                .withType(CharaChorderBean::class.java)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
            val csvUserIterator: MutableIterator<Any?> = csvToBean!!.iterator()
            while (csvUserIterator.hasNext()) {
                val csvUser: CharaChorderBean = csvUserIterator.next() as CharaChorderBean
                println(csvUser.output)
                //println("==========================")
            }
        }
        TODO("'root out' the prefix, infix, and/or suffix and add it to a new file that specifies input and finger " +
                "you would use to compound chord that chord + any character entry")
        /*val prefix = "de"
        println("Values: " + findValues(words, prefix))*/
        // TODO -- find words one can generate through suffixes as well
    }
}