import java.io.File
import kotlin.system.exitProcess
import kotlin.time.measureTime

fun main() {
    val inputTest = getInput("inputTest.txt")
    val input = getInput("input.txt")
    mainMeasuringTime({ solve(inputTest) }, "InputTest")
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

data class Pos(val row: Int, val col: Int)
typealias Grid = Map<Pos, Char>

data class Input(val grid: Map<Pos, Char>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val lines = file.split("\n").map { s -> s.map { it } }
    val map = lines.withIndex().flatMap { (row, charList) ->
        charList.withIndex().map { (col, char) ->
            Pos(row, col) to char
        }
    }.toMap()
    return Input(map)
}

fun solve(input: Input): Number {
    val start = input.grid.toList().find { (pos, char) -> char == 'S' }!!

    val steps = 64
    val grid = input.grid + (start.first to 'O')
    val maxCol = input.grid.maxBy { it.key.col }.key.col
    val maxRow = input.grid.maxBy { it.key.row }.key.row
    val gridResult = solveRec(grid, steps, maxCol = maxCol, maxRow = maxRow)

    return gridResult.entries.count { (_, char) -> char == 'O' }
}

fun Grid.print() {
    println()
    var currentRow = 0
    this.toList().forEach { (pos, char) ->
        if (pos.row != currentRow) {
            println()
            currentRow = pos.row
        }
        print(char)
    }
    println()
}

tailrec fun solveRec(grid: Grid, leftSteps: Int, maxCol: Int, maxRow: Int): Grid {
    grid.print()
    if (leftSteps == 0) return grid

    val startPoses = grid.filter { (_, char) -> char == 'O' }

    val toAdd = startPoses.flatMap { (pos, _) ->
        listOf(0 to 1, 0 to -1, 1 to 0, -1 to 0)
            .mapNotNull { (dRow, dCol) ->
                val dPos = pos.copy(row = pos.row + dRow, col = pos.col + dCol)
                if (dPos.outOfRange(maxCol, maxRow)) return@mapNotNull null

                val actual = grid[dPos]
                if (actual == '#') return@mapNotNull dPos to '#'
                return@mapNotNull dPos to 'O'
            }
    }
    val newStartPoses: Grid = startPoses.map { (pos, _) -> pos to '.' }.toMap()
    val newGrid: Grid = grid + newStartPoses + toAdd
    return solveRec(newGrid, leftSteps - 1, maxCol, maxRow)
}

fun Pos.outOfRange(maxCol: Int, maxRow: Int): Boolean =
    false
    //this.col <= 0 || this.row <= 0 || this.col > maxCol || this.row > maxRow
