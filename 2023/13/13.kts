import java.io.File
import kotlin.time.measureTime

fun main() {
    val inputTest = getInput("inputTest.txt")
    mainMeasuringTime({ solve(inputTest) }, "InputTest")
    val input = getInput("input.txt")
    mainMeasuringTime({ solve(input) }, "Input")
}
main()

data class Input(val patterns: List<List<String>>)

fun mainMeasuringTime(someFun: () -> Int, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint $funTime")

}

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val patterns = file.split("\n\n").map { it.split("\n") }
    println(patterns.map { it.joinToString("\n") }.joinToString("\n\n"))
    return Input(patterns)
}

fun solve(input: Input): Int {
    return input.patterns.map { pattern ->
        val reflectionRowIndex = getReflectionRowIndex(pattern)
        println("ReflectionRowIndex $reflectionRowIndex")
        val reflectionColIndex = getReflectionColIndex(pattern)
        println("ReflectionColIndex $reflectionColIndex")
        100 * (reflectionRowIndex) + reflectionColIndex
    }.sum()
}

fun getReflectionRowIndex(pattern: List<String>): Int {
    val foundPoint = (0..pattern.size).find { x ->
        val subListBefore = pattern.subList(0, x)
        val subListAfter = pattern.subList(x, pattern.size)
        if ((subListAfter.size == subListBefore.size)) {
            //part1: subListBefore.reversed() == subListAfter
            compareAllowExactlyOne(subListBefore.reversed(), subListAfter)
        } else {
            if (subListAfter.isEmpty() or subListBefore.isEmpty()) return@find false
            compareDifferentSize(subListAfter, subListBefore)
        }
    }
    return foundPoint ?: 0
}

fun getReflectionColIndex(pattern: List<String>): Int {
    val cols = pattern.mapIndexed { x, str ->
        str.mapIndexed { y, char ->
            Triple(x, y, char)
        }
    }.flatten().groupBy { (_, y, _) ->
        y
    }.map { it ->
        it.key to (it.value.map { (x, _, char) -> x to char }).sortedBy { it.first }
    }.sortedBy { it.first }

    val foundPoint = (0..pattern.first().length).find { y ->
        val subListBefore = cols.subList(0, y).map { it.second.map { (_, char) -> char }.joinToString("") }
        val subListAfter = cols.subList(y, pattern.first().length).map { it.second.map { (_, char) -> char}.joinToString("")}
        if ((subListAfter.size == subListBefore.size)) {
            //part1: subListBefore.reversed() == subListAfter
            compareAllowExactlyOne(subListBefore.reversed(), subListAfter)
        } else {
            if (subListAfter.isEmpty() or subListBefore.isEmpty()) return@find false
            compareDifferentSize(subListAfter, subListBefore)
        }
    }
    return foundPoint ?: 0
}

fun compareAllowExactlyOne(list: List<String>, otherList: List<String>): Boolean {
    val charDiffCount = list.zip(otherList).map { (line, otherLine) ->
        line.toList().zip(otherLine.toList()).filter { (char, otherChar) ->
            char != otherChar
        }.size
    }.sum()
    return charDiffCount == 1
}

fun compareDifferentSize(
    subListAfter: List<String>,
    subListBefore: List<String>
) = if (subListAfter.size > subListBefore.size) {
    // part1: subListAfter.dropLast(subListAfter.size - subListBefore.size).reversed() == subListBefore
    compareAllowExactlyOne(subListAfter.dropLast(subListAfter.size - subListBefore.size).reversed(), subListBefore)
} else {
    // part1: subListBefore.drop(subListBefore.size - subListAfter.size).reversed() == subListAfter
    compareAllowExactlyOne(subListBefore.drop(subListBefore.size - subListAfter.size).reversed(), subListAfter)
}