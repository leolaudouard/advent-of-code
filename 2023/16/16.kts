import _16.Side.*
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

data class Input(val list: List<String>)

enum class Side { R, L, T, B }
data class Key(val x: Int, val y: Int, val side: Side)

data class Visited(val visitedPoses: Set<Pair<Int, Int>>, val lasersHistory: List<Key>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val lines = file.split("\n")
    return Input(lines)
}

fun solve(input: Input): Number {
    val lasers = listOf(Key(-1, 0, R))
    val visited = Visited(setOf(-1 to 0), lasers)
    val visitedPoses2 = recurse(lasers, input, visited)
    /*
        var loop = true
        while (loop) {
            val (newLasers, newVisited) = travel(lasers, input.list, visited)
            //&& newVisited.lasersHistory.toList()
            //                .last() in visited.lasersHistory.toList()
            if (newVisited.keys().distinct() == visited.keys().distinct()
            ) {
                loop = false
            }
            lasers = newLasers.toMutableMap()
            //newVisited.keys().print(input.list.first().length, input.list.size)
            visited = newVisited
        }
    */

    val visitedPoses = visitedPoses2.lasersHistory
    visitedPoses.print(input.list.first().length, input.list.size)
    return visitedPoses.size(input.list.first().length, input.list.size)
}

tailrec fun recurse(lasers: List<Key>, input: Input, visited: Visited): Visited {
    val (newLasers, newVisited) = travel(lasers, input.list, visited)
    //newVisited.keys().print(input.list.first().length, input.list.size)
    println("Travalled visited poses: ${newVisited.visitedPoses.size}, lasers count: ${newLasers.size}")
    return if (newVisited.lasersHistory.distinct() == visited.lasersHistory.distinct()) {
        newVisited
    } else recurse(newLasers, input, newVisited)
}

fun travel(lasers: List<Key>, lines: List<String>, visited: Visited): Pair<List<Key>, Visited> {
    return lasers.fold(listOf<Key>() to visited) { (lasersAcc, visitedAcc), laser ->
        val newPos = when (laser.side) {
            T -> laser.x to laser.y - 1
            B -> laser.x to laser.y + 1
            R -> laser.x + 1 to laser.y
            L -> laser.x - 1 to laser.y
        }

        val newSides = getNewSides(newPos, lines, laser.side)
        val newLasers = newSides.map { side ->
            Key(newPos.first, newPos.second, side)
        }
        val currentHistory = visitedAcc.lasersHistory + newLasers
        val newVisited = visitedAcc.copy(
            visitedPoses = (visitedAcc.visitedPoses + newLasers.map { it.x to it.y }),
            //lasersHistory = visitedAcc.lasersHistory + (visitedAcc.lasersHistory + newLasers)
            lasersHistory = currentHistory
        )
        (lasersAcc + newLasers) to newVisited
    }
}


fun getNewSides(newPos: Pair<Int, Int>, lines: List<String>, side: Side): List<Side> {
    val char = lines.getOrNull(newPos.second)?.getOrNull(newPos.first)
    if (char == null) {
        //print("Stop because null char")
        //println("X: ${newPos.first}, Y: ${newPos.second}")
    }
    val newSides = when (char) {
        '/' -> when (side) {
            R -> listOf(T)
            L -> listOf(B)
            T -> listOf(R)
            B -> listOf(L)
        }

        '\\' -> when (side) {
            R -> listOf(B)
            L -> listOf(T)
            T -> listOf(L)
            B -> listOf(R)
        }

        '-' -> when (side) {
            R -> listOf(R)
            L -> listOf(L)
            T -> listOf(L, R)
            B -> listOf(L, R)
        }

        '|' -> when (side) {
            R -> listOf(T, B)
            L -> listOf(T, B)
            T -> listOf(T)
            B -> listOf(B)
        }

        '.' -> listOf(side)
        null -> listOf()
        else -> throw Exception("Unknown char $char")
    }
    //println("X: ${newPos.first}, Y: ${newPos.second}, newSides: $newSides")
    return newSides
}

fun List<Key>.print(maxX: Int, maxY: Int) {
    println()
    (0..maxY).map { y ->
        (0..maxX).map { x ->
            if (this.find { key -> key.x == x && key.y == y } != null) {
                print("#")
            } else print(".")
        }
        println()
    }
}

fun List<Key>.size(maxX: Int, maxY: Int): Int {
    var size = 0
    (0..maxY).map { y ->
        (0..maxX).map { x ->
            if (this.find { key -> key.x == x && key.y == y } != null) {
                size += 1
            }
        }
    }
    return size
}
