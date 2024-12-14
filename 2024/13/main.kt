package `13`

import java.io.File
import kotlin.time.measureTime

fun main() {
    val inputTest = getInput("inputTest.txt")
    val input = getInput("input.txt")
    println("-------- PART 1 --------")
    println(inputTest)
    println(input.lines.first())
    println(input.lines.last())
    //mainMeasuringTime({ part1(inputTest) }, "InputTest")
    //mainMeasuringTime({ part1(input) }, "Input")
    println("\n-------- PART 2 --------")
    mainMeasuringTime({ part2(inputTest) }, "InputTest")
    mainMeasuringTime({ part2(input) }, "Input")
}

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint exec time $funTime")

}

private fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val lines = file.split("\n\n").map { it.split("\n") }.map { threeLines ->
        val aX = threeLines[0].substringAfter("X+").substringBefore(",").toLong()
        val aY = threeLines[0].substringAfter("Y+").toLong()

        val bX = threeLines[1].substringAfter("X+").substringBefore(",").toLong()
        val bY = threeLines[1].substringAfter("Y+").toLong()

        val rX = threeLines[2].substringAfter("X=").substringBefore(",").toLong()
        val rY = threeLines[2].substringAfter("Y=").toLong()
        Line(aX, aY, bX, bY, rX, rY)
    }

    return Input(lines)
}

data class Input(val lines: List<Line>)
data class Line(val aX: Long, val aY: Long, val bX: Long, val bY: Long, val rX: Long, val rY: Long)
data class Result(val a: Long, val b: Long)


fun part1(input: Input): Long {
    val results = input.lines.map { line ->
        val (aX, aY, bX, bY, rX, rY) = line
        val ok = (0..100.toLong()).flatMap { a ->
            (0..100.toLong()).map { b ->
                Result(a, b)
            }
        }.filter { (a, b) -> a * aX + b * bX == rX && a * aY + b * bY == rY }

        ok.minByOrNull { (a, b) ->
            a * 3 + b
        }
    }
    println(results)
    return results.filterNotNull().sumOf { it.a * 3 + it.b }.toLong()
}

fun part2(input: Input): Long {
    val lines = input.lines.map {
        val newRX = ("10000000000000" + it.rX.toString()).toLong()
        val newRY = ("10000000000000" + it.rY.toString()).toLong()
        it.copy(rX = newRX, rY = newRY)
    }

    val results = lines.map { line ->
        val (aX, aY, bX, bY, rX, rY) = line
        val maxA = getMaxA(line)
        val maxB = getMaxB(line)
        println("Max A")
        println(maxA)
        println("Max B")
        println(maxB)
        val (minA, minB) = getMin(line, maxA, maxB)
        println("Min  A")
        println(minA)
        println("Min B")
        println(minB)
        (minA..maxA).flatMap { a ->
            (minB..maxB).map { b ->
                Result(a, b)
            }
        }.filter { (a, b) -> a * aX + b * bX == rX && a * aY + b * bY == rY }.minByOrNull { (a, b) ->
            a * 3 + b
        }
    }
    println(results)
    return results.filterNotNull().sumOf { it.a * 3 + it.b }.toLong()
}


private fun getMin(line: Line, maxA: Long, maxB: Long): Pair<Long, Long> {
    val (aX, aY, bX, bY, rX, rY) = line
    val minA = (0..maxA).find { a ->
        (0..maxB).find { b ->
            a * aX + b * bX == rX
                    && a * aY + b * bY == rY
        } != null
    }

    val minB = (0..maxB).find { b ->
        (0..maxA).find { a ->
            a * aX + b * bX == rX
                    && a * aY + b * bY == rY
        } != null
    }
    return minA!! to minB!!
}


private fun getMax(dX: Long, dY: Long, rX: Long, rY: Long): Long {
    return listOf((rX / dX) + 1, (rY / dY) + 1).max()
}

// a*aX + b*bX = rX
// b*aY + b*bY = rY
// min by 3*a + b
private fun getMaxA(line: Line): Long {
    return getMax(line.aX, line.aY, line.rX, line.rY)
}

private fun getMaxB(line: Line): Long {
    return getMax(line.bX, line.bY, line.rX, line.rY)
}
