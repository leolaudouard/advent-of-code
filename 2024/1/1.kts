import java.io.File
import kotlin.math.abs
import kotlin.time.measureTime

fun main() {
    val inputTest = getInput("inputTest.txt")
    val input = getInput("input.txt")
    println("-------- PART 1 --------")
    mainMeasuringTime({ part1(inputTest) }, "InputTest")
    mainMeasuringTime({ part1(input) }, "Input")
    println("\n-------- PART 2 --------")
    mainMeasuringTime({ part2(inputTest) }, "InputTest")
    mainMeasuringTime({ part2(input) }, "Input")
}
main()

data class Input(val firstList: List<Long>, val secondList: List<Long>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val splitted = file.split("\n").dropLast(1).map { line ->
        val splitLine = line.split("   ").map { it.toLong() }
        val first = splitLine.first()
        val second = splitLine.last()
        first to second
    }
    val firstList = splitted.map { it.first }
    val secondList = splitted.map { it.second }
    return Input(firstList, secondList)
}

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint exec time $funTime")

}

fun part1(input: Input): Long {
    return input.firstList.sorted().zip(input.secondList.sorted())
        .map { (first, second) -> abs(first - second) }
        .sum()
}

fun part2(input: Input): Long {
    val freq = input.secondList.groupingBy { it }.eachCount()
    return input.firstList.map { it * (freq[it] ?: 0) }.sum()
}