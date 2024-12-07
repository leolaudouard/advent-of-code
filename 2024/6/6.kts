import java.io.File
import kotlin.system.exitProcess
import kotlin.time.measureTime
import kotlin.math.round

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

data class Input(val grid: List<List<Char>>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val splitted = file.split("\n").dropLast(1)
    val grid = splitted.map { it.toCharArray().toList() }
    return Input(grid)
}

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint exec time $funTime")

}

fun part1(input: Input): Long {
    val maxY = input.grid.first().size - 1
    val maxX = input.grid.size - 1
    val point = getStartPoint(input)
    var x = point.x
    var y = point.y
    var dX = point.dX
    var dY = point.dY

    var visited = mutableSetOf<Pair<Int, Int>>(x to y)
    while (x + dX <= maxX && y + dY <= maxY  && x + dX >= 0 && y + dY >= 0) {
        val nextX = x + dX
        val nextY = y + dY
        val next = input.grid[nextY][nextX]

        if (next == '#') {
            //println("turnaround")
            val (newdX, newdY) = dY * -1 to  dX * 1
            dX = newdX
            dY = newdY
        } else {
            visited.add(nextX to nextY)
            x = nextX
            y = nextY
        }
    }
    return visited.size.toLong()
}


typealias Grid = List<List<Char>>
data class Point(val x: Int, val y: Int, val dX: Int, val dY: Int)

private fun isLoop(startPoint: Point, grid: Grid): Boolean {
    val maxY = grid.first().size - 1
    val maxX = grid.size - 1
    var currentX = startPoint.x
    var currentY = startPoint.y
    var dX = startPoint.dX
    var dY = startPoint.dY

    var visited = mutableSetOf<Point>(Point(currentX, currentY, dX, dY))
    while (currentX + dX <= maxX && currentY + dY <= maxY  && currentX + dX >= 0 && currentY + dY >= 0) {
        val nextX = currentX + dX
        val nextY = currentY + dY
        val next = grid[nextY][nextX]
        val point = Point(nextX, nextY, dX, dY)
        if (point in visited) return true

        if (next == '#') {
            val (newdX, newdY) = dY * -1 to  dX * 1
            dX = newdX
            dY = newdY
        } else {
            visited.add(point)
            currentX = nextX
            currentY = nextY
        }
    }
    return false

}

private fun getStartPoint(input: Input): Point {
    input.grid.withIndex().map { (y, charList) ->
        charList.withIndex().map { (x, value) ->
            if (value == '^') {
                return Point(x=x, y=y,dX= 0, dY=-1)
            }

        }
    }
    throw Exception("Start point not found")
}

fun part2(input: Input): Long {
    val startPoint = getStartPoint(input)
    return input.grid.withIndex().mapNotNull { (y, charList) ->
        charList.withIndex().mapNotNull { (x, char) ->
            if (char == '.') {
                val newGrid = input.grid.map { it.toMutableList() }.toMutableList()
                newGrid[y][x] = '#'
                if (isLoop(startPoint, newGrid)) 1 else null
            } else null

        }
    }.flatten().size.toLong()
}
