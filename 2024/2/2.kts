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

data class Input(val reports: List<List<Long>>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val splitted = file.split("\n").dropLast(1).map { line ->
        line.split(" ").map { it.toLong() }
    }
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
    return input.reports.filter { report ->
        report.isSafe()
    }.size.toLong()
}

fun part2(input: Input): Long {
    val (safe, unsafe) = input.reports.partition { report ->
        report.isSafe()
    }

    val newSafe = unsafe.filter { report ->
        val index = report.indices.find { indexToRemove ->
            val newReport = report.filterIndexed { index, _ -> index != indexToRemove }
            newReport.isSafe()
        }
        index != null
    }
    return (safe.size + newSafe.size).toLong()
}

data class Acc(val prev: Int, val dropped: Boolean, val diff: Int)

private fun List<Long>.isSafe(): Boolean {
    val decreasingSafe = this.zipWithNext().all { (prev, next) ->
        val diff = prev - next
        diff != 0.toLong() && diff > 0 && diff <= 3
    }
    val increasingSafe = this.zipWithNext().all { (prev, next) ->
        val diff = prev - next
        diff != 0.toLong() && diff < 0 && diff >= -3
    }
    return decreasingSafe || increasingSafe
}