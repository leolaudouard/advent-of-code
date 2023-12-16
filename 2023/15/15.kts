import java.io.File
import kotlin.time.measureTime

fun main() {
    val inputTest = getInput("inputTest.txt")
    mainMeasuringTime({ solve(inputTest) }, "InputTest")
    val input = getInput("input.txt")
    mainMeasuringTime({ solve(input) }, "Input")
}
main()


fun mainMeasuringTime(someFun: () -> Number, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint $funTime")

}

data class Input(val list: List<Line>)

data class Line(val str: String, val symbol: Symbol, val box: Long, val label: String)
sealed class Symbol
data object Minus : Symbol()
data class Plus(val focalLength: Int) : Symbol()

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val list = file.split("\n").first().split(",").map { str ->
        val symbol = str.symbol()
        val label = if (symbol is Plus) str.substringBefore("=") else str.substringBefore("-")
        Line(str, str.symbol(), label.hash(), label)
    }
    return Input(list)
}

fun String.symbol(): Symbol {
    val focalLengthOrNull = this.last().digitToIntOrNull()
    return focalLengthOrNull?.let { Plus(it) } ?: Minus
}

fun solvePartOne(input: Input): Long {
    val results = input.list.map { it.str.hash() }
    println(results.joinToString("\n"))
    return results.sum()
}

fun solve(input: Input): Long {
    val boxes = input.list.fold(mapOf<Long, List<Line>>()) { acc, line ->
        when (line.symbol) {
            Minus -> handleMinus(line, acc)
            is Plus -> handlePlus(line, acc)
            else -> TODO()
        }
    }
    println(boxes.toList().map(toStr()).joinToString("\n"))
    return boxes.toList().sumOf { (box, lines) ->
        lines.withIndex().mapNotNull { (index, line) -> if (line.symbol is Plus) line.symbol.focalLength * (index +1L) * (box+1) else 0L }.sum()
    }
}

fun String.hash(): Long = this.fold(0L) { acc, char ->
    (acc + char.code) * 17 % 256
}

fun handlePlus(line: Line, acc: Map<Long, List<Line>>): Map<Long, List<Line>> {
    val boxLines = acc[line.box] ?: listOf()
    val currentLineIndex = boxLines.withIndex().find { (index, l) ->
        l.label == line.label
    }?.index
    val newLines = boxLines.toMutableList()
    if (currentLineIndex != null) {
        newLines[currentLineIndex] = line
    } else newLines.add(boxLines.size, line)
    return acc + mapOf(line.box to newLines)
}

fun handleMinus(line: Line, acc: Map<Long, List<Line>>): Map<Long, List<Line>> {
    val boxLines = acc[line.box] ?: listOf()
    val newLines = boxLines.filter { it.label != line.label }
    return acc + mapOf(line.box to newLines)
}

fun List<Line>.toStr(): List<String> {
    return this.map {
        it.label + " " + it.symbol.toStr()
    }
}

fun Symbol.toStr(): String = when (this) {
    Minus -> ""
    is Plus -> this.focalLength.toString()
    else -> TODO()
}

fun toStr(): (Pair<Long, List<Line>>) -> Pair<Long, List<String>> =
    {
        it.first to it.second.toStr()
    }