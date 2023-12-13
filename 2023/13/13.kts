import java.io.File
import kotlin.math.absoluteValue
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
/*
To summarize your pattern notes, add up the number of columns to the left of each vertical line of reflection; to that, also add 100 multiplied by the number of rows above each horizontal line of reflection. In the above example, the first pattern's vertical line has 5 columns to its left and the second pattern's horizontal line has 4 rows above it, a to
1 #...##..# 1
2 #....#..# 2
3 ..##..### 3
4v#####.##.v4
5^#####.##.^5
6 ..##..### 6
7 #....#..# 7
 */

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
            subListBefore.reversed() == subListAfter
        } else {
            if (subListAfter.isEmpty() or subListBefore.isEmpty()) return@find false
            //println("Should pass here")
            //println("Sublist before")
            //println(subListBefore.joinToString("\n"))
            //println("Sublist after")
            //println(subListAfter.joinToString("\n"))
            if (subListAfter.size > subListBefore.size) {
                subListAfter.dropLast(subListAfter.size - subListBefore.size).reversed() == subListBefore
            } else subListBefore.drop(subListBefore.size - subListAfter.size).reversed() == subListAfter
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
        val subListBefore = cols.subList(0, y).map { it.second }
        val subListAfter = cols.subList(y, pattern.first().length).map { it.second }
        if ((subListAfter.size == subListBefore.size)) {
            subListBefore.reversed() == subListAfter
        } else {
            if (subListAfter.isEmpty() or subListBefore.isEmpty()) return@find false
            //println("Should pass here")
            //println("Sublist before")
            //println(subListBefore.joinToString("\n"))
            //println("Sublist after")
            //println(subListAfter.joinToString("\n"))
            if (subListAfter.size > subListBefore.size) {
                subListAfter.dropLast(subListAfter.size - subListBefore.size).reversed() == subListBefore
            } else subListBefore.drop(subListBefore.size - subListAfter.size).reversed() == subListAfter
        }
    }
    return foundPoint ?: 0
}