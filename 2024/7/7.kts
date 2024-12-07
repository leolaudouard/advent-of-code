import java.io.File
import kotlin.system.exitProcess
import kotlin.time.measureTime
import kotlin.math.round

fun main() {
    val inputTest = getInput("inputTest.txt")
    val input = getInput("input.txt")
    println("-------- PART 1 --------")
    println(inputTest)
    println(input.lines.first())
    println(input.lines.last())
    mainMeasuringTime({ part1(inputTest) }, "InputTest")
    mainMeasuringTime({ part1(input) }, "Input")
    println("\n-------- PART 2 --------")
    mainMeasuringTime({ part2(inputTest) }, "InputTest")
    mainMeasuringTime({ part2(input) }, "Input")
}
main()

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint exec time $funTime")

}

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val splitted = file.split("\n").dropLast(1)
    val lines = splitted.map { line ->
        val result = line.substringBefore(":").toLong()
        val numbers = line.substringAfter(": ").split(" ").map { it.toLong() }
        Line(result, numbers)
    }
    return Input(lines)
}

data class Input(val lines: List<Line>)
data class Line(val result: Long, val numbers: List<Long>)

fun part1(input: Input): Long {
    return input.lines.filter { (result, numbers) ->
        val solution = findSolution(result, numbers) { value, b ->
            listOf(value + b, value * b)
        }

        solution != null
    }.map { (result, _) -> result }.sum()
}

fun part2(input: Input): Long {
    return input.lines.filter { (result, numbers) ->
        val solution = findSolution(result, numbers) { value, b ->
            listOf(value + b, value * b, concatenation(value, b))
        }
        solution != null
    }.map { (result, _) -> result }.sum()
}

private fun findSolution(result: Long, numbers: List<Long>, buildNewAcc: (Long, Long) -> List<Long>): Long? {
    numbers.drop(1).fold(listOf<Long>(numbers.first())) { acc, b ->
        val newAcc = acc.map { value -> buildNewAcc(value, b) }.flatten().filter { value -> value <= result }
        if (result in newAcc && b == numbers.last()) return@findSolution result
        newAcc
    }
    return null
}

private fun concatenation(a: Long, b: Long): Long {
    return "$a$b".toLong()

}
