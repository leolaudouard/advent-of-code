import java.io.File
import kotlin.math.abs
import kotlin.math.pow

val inputTest = File("inputTest.txt").readText()
val input = File("input.txt").readText()
val linesTest = inputTest.split('\n')
val lines = input.split('\n')


fun main() {
    val testResult = solve(linesTest)
    println("TestResult $testResult")
    val result = solve(lines)
    println("result $result")
}
main()


data class Line(val id: Int, val winners: List<Int>, val numbers: List<Int>)
typealias ScratchCards = Map<Int, Int>

fun solve(lines: List<String>): Int {
    val parsed = lines.fold(mapOf<Int, Int>()) { acc, line ->
        parse(acc, line)
    }
    return parsed.map { it.value }.sum()
}

/*
    return parsed.map { line ->
        val winningNumbersCount = line.numbers.filter { number ->
            number in line.winners
        }.size
        if (winningNumbersCount == 0) {
            0.0
        } else if (winningNumbersCount == 1) {
            1.0
        } else 2.toDouble().pow(winningNumbersCount - 1.toDouble())
    }.map {
        println(it)
        it
    }.sum()
*/

fun parse(scratchCards: ScratchCards, line: String): ScratchCards {
    val id = getId(line)
    val newScratchCard = inc(id, scratchCards.toMutableMap())
    val winningNumbers = getWinningNumbers(line)
    val numbers = getNumbers(line)
    val count = numbers.filter { number ->
        number in winningNumbers
    }.size
    return (1..count).fold(newScratchCard) { map, idToAdd ->
        inc(id + idToAdd, map.toMutableMap(), newScratchCard.getOrDefault(id, 1))
    }
}

fun inc(key: Int, map: Map<Int, Int>, inc: Int = 1): ScratchCards {
    val newMap = map.toMutableMap()
    newMap[key] = map.getOrDefault(key, 0) + inc
    return newMap.toMap()
}

fun getId(line: String): Int =
    line.substringBefore(":").substringAfter("Card ").filter { it.digitToIntOrNull() != null }.toInt()

fun getWinningNumbers(line: String): List<Int> {
    return line.substringAfter(":").substringBefore("|").split(" ").mapNotNull {
        it.toIntOrNull()
    }
}

fun getNumbers(line: String): List<Int> {
    return line.substringAfter("|").split(" ").mapNotNull {
        it.toIntOrNull()
    }
}
/*
    return winningNumberList.foldIndexed(listOf<Int>()) { index, acc, char ->
        val rest: List<Int> = (1..3).map {
           winningNumberList.getOrNull(index + it)?.toIntOrNull()
        }.takeWhile { it != null }.let {
            println(it)
            it
        }.filterNotNull()
        char.toIntOrNull()?.let {
            println(rest)
            acc + listOf((rest + it).joinToString("").toInt())
        } ?: acc
    }
*/
