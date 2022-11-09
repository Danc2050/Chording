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
                println("Value: " + `val` + ", " + val2 + " repeated")
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
        // TODO -- read in dictionary or list of words
        val words: List<String> = Arrays.asList("dog", "deer", "death", "death", "deal", "apple")
        // TODO -- iterate through list of prefixes and find the values in the dictionary that can be printed with them
        val prefix: String = "de"
        println("Values: " + findValues(words, prefix))
        // TODO -- find words one can generate through suffixes as well
    }
}