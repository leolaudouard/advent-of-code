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

data class Input(val str: String)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    return Input(file)
}

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint exec time $funTime")

}

fun part1(input: Input): Long {
    val regex = """mul\(\d+,\d+\)""".toRegex()
    val matches = regex.findAll(input.str)
    val pairs = matches.toList().map {
        it.value.toMulPair()
    }
    return pairs.sumOf { (a, b) ->
        a * b
    }
}

fun part2(input: Input): Long {
    val regex = """mul\(\d+,\d+\)|(don\'t)|(do)""".toRegex()
    val matches = regex.findAll(input.str)
    return matches.toList().fold(Acc()) { acc, match ->
        when (val value = match.value) {
            "do" -> acc.copy(take = true)
            "don't" -> acc.copy(take = false)
            else -> if (acc.take) acc.copy(list = acc.list + value.toMulPair()) else acc
        }
    }.list.sumOf { (a, b) -> a * b }
}


private fun String.toMulPair(): Pair<Long, Long> {
    val first = this.substringBefore(",").substringAfter("""mul(""").toLong()
    val second = this.substringAfter(",").substringBefore(""")""").toLong()
    return first to second
}

data class Acc(val list: List<Pair<Long, Long>> = listOf(), val take: Boolean = true)