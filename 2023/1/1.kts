import java.io.File
import kotlin.system.exitProcess

val input = File("input.txt").readText()
val stringIntMap = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
)
val lines = input.split('\n').dropLast(1)
val resultPart1 = lines.sumOf {
    getNumberPart1(it)
}
val resultPart2 = lines.sumOf {
    getNumberPart2(it, stringIntMap)
}
println("Result part one: $resultPart1")
println("Result part two: $resultPart2")

fun getNumberPart1(string: String): Int = string.mapNotNull { char -> char.toString().toIntOrNull() }.let { ints ->
    (ints.first().toString() + ints.last().toString()).toInt()
}

fun getNumberPart2(string: String, stringIntMap: Map<String, Int>): Int {
    val formattedStr = formatStrToInts(string, stringIntMap)
    val ints = formattedStr.mapNotNull { it.digitToIntOrNull() }
    return (ints.first().toString() + ints.last().toString()).toInt()
}

data class ParseResult(val int: Int, val index: Int, val str: String)

fun formatStrToInts(strToRead: String, stringIntMap: Map<String, Int>): String {
    return formatStrForFirst(strToRead, stringIntMap) + formatStrForLast(strToRead, stringIntMap)
}

fun formatStrForFirst(strToParse: String, stringIntMap: Map<String, Int>): String =
    stringIntMap.mapNotNull { (str: String, int: Int) ->
        if (strToParse.contains(str)) {
            val firstIndex = strToParse.indexOf(str)
            ParseResult(int, firstIndex, str)
        } else null
    }.minByOrNull { it.index }.let {
        when (it) {
            null -> strToParse
            else -> formatStrForFirst(strToParse.replaceFirst(it.str, it.int.toString()), stringIntMap)
        }
    }

fun formatStrForLast(strToParse: String, stringIntMap: Map<String, Int>): String =
    stringIntMap.mapNotNull { (str: String, int: Int) ->
        if (strToParse.contains(str)) {
            val lastIndex = strToParse.lastIndexOf(str)
            ParseResult(int, lastIndex, str)
        } else null
    }.maxByOrNull { it.index }.let {
        when (it) {
            null -> strToParse
            else -> formatStrForFirst(strToParse.replace(it.str, it.int.toString()), stringIntMap)
        }
    }