import java.io.File
import kotlin.math.abs
import kotlin.time.measureTime

fun main() {
    val inputTest = getInput("inputTest.txt")
    val input = getInput("input.txt")
    println("-------- PART 1 --------")
    println(inputTest)
    mainMeasuringTime({ part1(inputTest) }, "InputTest")
    mainMeasuringTime({ part1(input) }, "Input")
    println("\n-------- PART 2 --------")
    mainMeasuringTime({ part2(inputTest) }, "InputTest")
    mainMeasuringTime({ part2(input) }, "Input")
}
main()

data class Input(val lines: List<String>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val splitted = file.split("\n").dropLast(1)
    return Input(splitted)
}

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint exec time $funTime")

}

fun part1(input: Input): Long {
    return input.lines.mapIndexed { i, line ->
        line.mapIndexed { j, char ->
            if (char == 'X') {
                xmasCount(i, j, input)
            } else null
        }
    }.flatten().filterNotNull().sum().toLong()
}

fun part2(input: Input): Long {
    return input.lines.mapIndexed { i, line ->
        line.mapIndexed { j, char ->
            masCount(i, j, input)
        }
    }.flatten().sum().toLong()
}

private fun xmasCount(i: Int, j: Int, input: Input): Int {
    return listOf(
        xmas(i, j, input, 1, 0),
        xmas(i, j, input, -1, 0),
        xmas(i, j, input, 0, 1),
        xmas(i, j, input, 0, -1),
        xmas(i, j, input, 1, 1),
        xmas(i, j, input, -1, -1),
        xmas(i, j, input, 1, -1),
        xmas(i, j, input, -1, 1),
    ).filter { it == true }.size
}

private fun xmas(i: Int, j: Int, input: Input, addI: Int, addJ: Int): Boolean {
    val maybeM = input.lines.getOrNull(i + addI)?.getOrNull(j + addJ)
    val maybeA = input.lines.getOrNull(i + 2*addI)?.getOrNull(j + 2*addJ)
    val maybeS = input.lines.getOrNull(i + 3*addI)?.getOrNull(j + 3*addJ)
    val str = "$maybeM$maybeA$maybeS"
    return str == "MAS"
}

private fun masCount(i: Int, j: Int, input: Input): Int {
    val topLeft = input.lines.getOrNull(i)?.getOrNull(j)
    val maybeA = input.lines.getOrNull(i + 1)?.getOrNull(j + 1)
    val bottomRight = input.lines.getOrNull(i + 2)?.getOrNull(j + 2)

    val topRight = input.lines.getOrNull(i)?.getOrNull(j + 2)
    val bottomLeft = input.lines.getOrNull(i + 2)?.getOrNull(j)

    val str = "$topLeft$maybeA$bottomRight"
    val otherStr = "$topRight$maybeA$bottomLeft"
    return if ((str in listOf("MAS", "SAM")) && (otherStr in listOf("MAS", "SAM"))) 1 else 0
}
