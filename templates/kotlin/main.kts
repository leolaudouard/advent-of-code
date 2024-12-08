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
    return Input(splitted)
}

data class Input(val lines: List<String>)

fun part1(input: Input): Long {
    return TODO()
}

fun part2(input: Input): Long {
    return TODO()
}
