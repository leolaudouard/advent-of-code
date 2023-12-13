import java.io.File
import kotlin.time.measureTime

fun main() {
    val inputTest = Input(listOf(Line("???...###", listOf(1, 1, 3))))
    mainMeasuringTime({ solve(inputTest) }, "InputTest")
    //val input = getInput("input.txt")
    //mainMeasuringTime({ solve(input) }, "Input")
}
main()

data class Input(val lines: List<Line>)
data class Line(val str: String, val contiguousGroups: List<Int>)

fun mainMeasuringTime(someFun: () -> Int, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint $funTime")

}

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val splitted = file.split("\n").map { line ->
        val group = line.split(" ")[0]
        val contiguousGroup = line.split(" ")[1].split(",").map { char -> char.toInt() }
        Line(group, contiguousGroup)
    }
    return Input(splitted)
}

fun solve(input: Input): Int {
    return input.lines.map { line ->
        val count = getCombinationCount(line)
        println(count)
        count
    }.sum()
}

fun getCombinationCount(line: Line): Int {
    return recursiveFun(line.str, line.contiguousGroups, 0)
}

fun recursiveFun(strRest: String, remainingGroups: List<Int>, currentCount: Int = 0): Int {
    println("Remaining groups $remainingGroups")
    val currentGroup = remainingGroups.firstOrNull() ?: return 1
    if (strRest.length < remainingGroups.sum()) return 0
    val currentChar =
        strRest.firstOrNull() ?: if (currentCount == currentGroup && remainingGroups.size == 1) return 1 else return 0
    val rest = strRest.toList().drop(1).joinToString("")
    println(rest)
    return when (currentChar) {
        '.' -> handlePoint(currentGroup, currentCount, rest, remainingGroups)
        '#' -> handleH(rest, remainingGroups, currentCount, currentGroup)
        '?' -> recursiveFun(".$rest", remainingGroups, currentCount) + recursiveFun(
            "#$rest",
            remainingGroups,
            currentCount,
        )

        else -> throw Exception("Boom")
    }
}

fun handlePoint(currentGroup: Int, currentCount: Int, rest: String, remainingGroups: List<Int>) =
    if (currentCount == currentGroup) recursiveFun(
        strRest = rest,
        remainingGroups = remainingGroups.drop(1),
        currentCount = 0
    ) else recursiveFun(
        strRest = rest,
        remainingGroups = remainingGroups,
        currentCount = 0
    )

fun handleH(
    rest: String,
    remainingGroups: List<Int>,
    currentCount: Int,
    currentGroup: Int
) = if (currentCount == currentGroup) recursiveFun(
    strRest = rest,
    remainingGroups = remainingGroups.drop(1),
    currentCount = currentCount + 1
) else recursiveFun(
    strRest = rest,
    remainingGroups = remainingGroups,
    currentCount = currentCount + 1
)