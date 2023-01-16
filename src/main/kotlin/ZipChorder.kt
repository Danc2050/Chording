import java.nio.file.Files
import java.nio.file.Paths
fun main() {
    /* TODO -- This is a small sniippet of code that writes out zipchorder format based on this website: https://7esl.com/common-three-letter-words/

     */
    var moreThanOneLetter = 0
    Files.newBufferedReader(Paths.get("src\\main\\resources\\wikipediaroots")).use { reader ->
    //Files.newBufferedReader(Paths.get("src\\main\\resources\\prefix")).use { reader ->
        for (line in reader.lines()) {
            if (line.toSet().size < line.length) {
                moreThanOneLetter++
            }
            println("${line.toSet().joinToString("")}\t${line}")
        }
    }
    println(moreThanOneLetter)

//        Files.newBufferedReader(Paths.get("src\\main\\resources\\3letterWords")).use { reader ->
//            for (line in reader.lines()) {
//                println("${line.toSet().joinToString("")}\t${line}")
//            }
//        }

//        Files.newBufferedReader(Paths.get("src\\main\\resources\\3letterPronouns")).use { reader ->
//            for (line in reader.lines()) {
//                println("${line.toSet().joinToString("")}\t${line}")
//            }
//        }

//        Files.newBufferedReader(Paths.get("src\\main\\resources\\3letterVerbs")).use { reader ->
//            for (line in reader.lines()) {
//                println("${line.toSet().joinToString("")}\t${line}")
//            }
//        }

//        Files.newBufferedReader(Paths.get("src\\main\\resources\\3letterAdjectives")).use { reader ->
//            for (line in reader.lines()) {
//                println("${line.toSet().joinToString("")}\t${line}")
//            }
//        }

//        Files.newBufferedReader(Paths.get("src\\main\\resources\\3letterConjunctions")).use { reader ->
//            for (line in reader.lines()) {
//                println("${line.toSet().joinToString("")}\t${line}")
//            }
//        }

//        Files.newBufferedReader(Paths.get("src\\main\\resources\\3letterAdverbs")).use { reader ->
//            for (line in reader.lines()) {
//                println("${line.toSet().joinToString("")}\t${line}")
//            }
//        }

//        Files.newBufferedReader(Paths.get("src\\main\\resources\\3letterInterjections")).use { reader ->
//            for (line in reader.lines()) {
//                println("${line.toSet().joinToString("")}\t${line}")
//            }
//        }

    // TODO -- write out the Three Letter Article "the" and the Three Letter Preposition "Out" to zipchorder dictionary file

}
