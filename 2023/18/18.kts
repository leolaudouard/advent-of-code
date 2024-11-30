import java.io.File
import kotlin.math.absoluteValue
import kotlin.math.pow
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

data class Input(val lines: List<Line>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val rawLines = file.split("\n")
    val lines = rawLines.map { line ->
        parse(line)

    }
    return Input(lines)
}

data class Line(val d: Direction, val count: Long, val color: String)

fun parsePartOne(line: String): Line {
    val splitted = line.split(" ")
    val d = splitted.first().let {
        when (it) {
            "R" -> Direction.R
            "D" -> Direction.D
            "L" -> Direction.L
            "U" -> Direction.U
            else -> throw Exception("unknown $it")
        }
    }
    val count = splitted[1].toInt()
    val color = splitted[2].substringAfter("#").substringBefore("")
    return Line(d, count.toLong(), color)
}

fun parse(line: String): Line {
    val splitted = line.split(" ")
    //Each hexadecimal code is six hexadecimal digits long. The first five hexadecimal digits encode the distance in meters as a five-digit hexadecimal number.
    // The last hexadecimal digit encodes the direction to dig:
    // 0 means R, 1 means D, 2 means L, and 3 means U.
    val color = splitted.last().substringAfter("#").substringBefore(")")
    val d = color.last().let {
        when (it) {
            '0' -> Direction.R
            '1' -> Direction.D
            '2' -> Direction.L
            '3' -> Direction.U
            else -> throw Exception("unknown $it")
        }
    }
    val count = color.dropLast(1).mapIndexed { index, value ->
        16.toDouble().pow(4-index)*value.toInt2()
    }.sum().toLong()
    println("d: $d, count: $count")
    return Line(d, count, color)
}


fun Char.toInt2(): Long = when (this) {
    'a' -> 10
    'b' -> 11
    'c' -> 12
    'd' -> 13
    'e' -> 14
    'f' -> 15
    else -> this.digitToInt().toLong()
}

enum class Direction {
    U, D, L, R
}

data class Pos(val x: Long, val y: Long)

fun solve(input: Input): Number {
    val polygonPoints = input.lines.fold(listOf(Pos(0, 0))) { acc, line ->
        val previous = acc.last()
        acc + when (line.d) {
            Direction.U -> previous.copy(y = previous.y + line.count)
            Direction.D -> previous.copy(y = previous.y - line.count)
            Direction.R -> previous.copy(x = previous.x + line.count)
            Direction.L -> previous.copy(x = previous.x - line.count)
        }
    }
    val area = area(polygonPoints)

    val b = input.lines.sumOf { it.count }
    println("Area is ${area}, perimeter is $b")
    /*
    A is the area,
    i is the number of internal points,
    b is the number of boundary points.
    Pick's gives:
    A = i + b / 2 - 1,
    -> i = A - b / 2 + 1.

    What we want is the i + the perimeter. i.e. b.
    result = A - b / 2 + 1 + b
    result = A + b / 2 + 1
     */
    return area + (b / 2) + 1
}

fun area(points: List<Pos>): Long {
    /*
    Shoelace gives:
     2A = sum(x(n) * y(n+1) - x(n+1)*y(n)) for n in each polygon point
     */
    val segments = points.zipWithNext() + (points.last() to points.first())
    return (segments.map { (prev, next) ->
        prev.x * next.y - next.x * prev.y
    }.sum() / 2).absoluteValue
}