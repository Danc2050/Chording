import org.jsoup.Jsoup
import java.io.File
/*
    A bunch of helper functions for gathering the roots and prefixes
 */
class helperFunctions {
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
}