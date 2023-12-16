import java.io.File
import kotlin.time.measureTime

fun main() {
    val inputTest = getInput("inputTest.txt")
    mainMeasuringTime({ solve(inputTest) }, "InputTest")
    val input = getInput("input.txt")
    mainMeasuringTime({ solve(input) }, "Input")
}
main()


fun mainMeasuringTime(someFun: () -> Int, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint $funTime")

}

data class Input(val grid: Grid<Symbol>)
typealias Grid<T> = Map<Pos, T>

data class Pos(val x: Int, val y: Int)
enum class Symbol {
    RollRock, CubeRock, Empty
}

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val lines = file.split("\n")
    val grid = lines.mapIndexed { y, str ->
        str.mapIndexed { x, char ->
            val pos = Pos(x, y)
            pos to char.toSymbol()
        }
    }.flatten().toMap()

    val gridCycled = grid.multiCycle(1000000000)
    grid.print()
    return Input(gridCycled)
}

fun Grid<Symbol>.print() {
    this.toList().groupBy { (p, _) -> p.y }
        .map { (_, posToSymbol) ->
            println()
            posToSymbol.map { (_, s) ->
                print(s.toChar())
            }
        }
}

fun Symbol.toChar(): Char = when (this) {
    Symbol.Empty -> '.'
    Symbol.CubeRock -> '#'
    Symbol.RollRock -> 'O'
}

fun solve(input: Input): Int {
    val maxY: Int = input.grid.toList().maxBy { (p, _) -> p.y }.first.y + 1
    println("Go sumOf $maxY")
    val sumList = input.grid.toList().filter { (_, s) ->
        s == Symbol.RollRock
    }.groupBy { (p, _) -> p.y }.map { (y, list) -> y to list.size }
    println("Sumlist ${sumList.joinToString("\n")}")
    val sum = sumList.sumOf { (y, count) ->
        (maxY - y) * count
    }
    return sum
}

fun Char.toSymbol() = when (this) {
    '.' -> Symbol.Empty
    '#' -> Symbol.CubeRock
    'O' -> Symbol.RollRock
    else -> throw Exception("Unknown symbol $this")
}


fun Grid<Symbol>.multiCycle(count: Int = 1): Grid<Symbol> {
    val gridHashCodes = mutableListOf(this.hashCode())
    var grid = this
    var loop = true
    var i = 0
    var patternSize: Int? = null
    while (loop) {
        i++
        if (i == count) loop = false
        val directions = listOf('N', 'W', 'S', 'E')
        val newAcc = directions.fold(grid) { gridAcc, dir ->
            val g = gridAcc.tilt(dir)
            gridHashCodes.add(gridHashCodes.size, g.hashCode())
            g
        }
        if (gridHashCodes.isNotEmpty()) {
            patternSize = shouldStopLoop(gridHashCodes.toList())
            if (patternSize != null) {
                loop = false
            }
        }

        grid = newAcc
    }
    var rest = i
    println("Loop stopped at $i.")
    var loop2 = true
    while (loop2) {
        val newRest = rest + patternSize!!
        if (newRest <= count) {
            rest = newRest
        } else {
            loop2 = false
        }
    }
    println("Jump to $rest")
    // Loop stopped at 2. Rest is 1, 999999998
    return (rest+1..count).fold(grid) { acc, _ ->
        val directions = listOf('N', 'W', 'S', 'E')
        val newAcc = directions.fold(acc) { grid, dir ->
            grid.tilt(dir)
        }
        newAcc
    }
}

fun Grid<Symbol>.tilt(dir: Char): Grid<Symbol> = this.toList().fold(this) { acc, (pos, symbol) ->
    if (symbol == Symbol.RollRock) roll(acc, pos, dir) else acc
}

fun roll(acc: Grid<Symbol>, pos: Pos, dir: Char): Map<Pos, Symbol> {
    val isVertical = (dir == 'N' || dir == 'S')
    val sorted = acc.toList().filter { (p, _) ->
        if (isVertical) pos.x == p.x else pos.y == p.y
    }.sortedBy { (_, _) ->
        if (isVertical) pos.y else pos.x
    }
    val subList = when (dir) {
        'N' -> sorted.subList(0, pos.y)
        'S' -> sorted.subList(pos.y, sorted.size)
        'W' -> sorted.subList(0, pos.x)
        'E' -> sorted.subList(pos.x, sorted.size)
        else -> throw Exception("Unknown dir $dir")
    }
    val emptySymbolsList = subList.maybeReversed(dir).takeWhile { (_, s) -> s == Symbol.Empty || s == Symbol.RollRock }
        .filter { (_, s) -> s == Symbol.Empty }
    val newPos = emptySymbolsList.lastOrNull()?.let { (p, _) -> p }
    return if (newPos != null) {
        acc.plus(newPos to Symbol.RollRock).plus(pos to Symbol.Empty)
    } else acc
}

fun <T> List<T>.maybeReversed(dir: Char): List<T> =
    when (dir) {
        'W',
        'N' -> this.reversed()

        'S',
        'E' -> this

        else -> throw Exception("Unknown dir $dir")
    }


fun <T>shouldStopLoop(grids: List<T>): Int? {
    val last = grids.last()
    val patternWithoutLast = grids.dropLast(1).takeLastWhile { it != last }
    val pattern = patternWithoutLast + last
    val to = grids.size - pattern.size
    val from = to - pattern.size

    return if (from >= 0) {
        val beforePattern = grids.subList(from, to)
        if (beforePattern == pattern) {
            pattern.size
        } else null
    } else null
}
