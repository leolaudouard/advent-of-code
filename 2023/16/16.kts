import _16.Side.*
import java.io.File
import kotlin.time.measureTime

fun main() {
    val inputTest = getInput("inputTest.txt")
    mainMeasuringTime({ solve(inputTest) }, "InputTest")
    //val input = getInput("input.txt")
    //mainMeasuringTime({ solve(input) }, "Input")
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

data class Visited(val visitedPoses: Set<Pair<Int, Int>>, val lasersHistory: List<Map<Key, Int>>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val lines = file.split("\n")
    return Input(lines)
}

fun solve(input: Input): Number {
    val lasers = mutableMapOf(Key(-1, 0, R) to 1)
    val visited = Visited(setOf(0 to 0), listOf(lasers))
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

    val visitedPoses = visitedPoses2.keys()
    visitedPoses.print(input.list.first().length, input.list.size)
    return visitedPoses.size(input.list.first().length, input.list.size)
}

tailrec fun recurse(lasers: Map<Key, Int>, input: Input, visited: Visited): Visited {
    val (newLasers, newVisited) = travel(lasers, input.list, visited)
    newVisited.keys().print(input.list.first().length, input.list.size)
    println("Travalled visited poses: ${newVisited.visitedPoses.size}, lasers count: ${newLasers.keys.size}")
    return if (newVisited.keys().distinct() == visited.keys().distinct() ) {
        newVisited
    } else recurse(newLasers, input, newVisited)
}

fun Visited.keys(): List<Key> = this.lasersHistory.flatMap { it.toList() }.groupBy { it.first }.map { it.key }


fun travel(lasers: Map<Key, Int>, lines: List<String>, visited: Visited): Pair<Map<Key, Int>, Visited> {
    return lasers.toList().fold(mapOf<Key, Int>() to visited) { (lasersAcc, visitedAcc), laser ->
        val newPos = when (laser.first.side) {
            T -> laser.first.x to laser.first.y - 1
            B -> laser.first.x to laser.first.y + 1
            R -> laser.first.x + 1 to laser.first.y
            L -> laser.first.x - 1 to laser.first.y
        }

        val newSides = getNewSides(newPos, lines, laser.first.side)
        val newLasers = newSides.map { side ->
            Key(newPos.first, newPos.second, side) to 1
        }.toMap()
        val currentHistory = (visitedAcc.lasersHistory.lastOrNull() ?: mapOf()) + newLasers
        val newVisited = visitedAcc.copy(
            visitedPoses = (visitedAcc.visitedPoses + newLasers.map { it.key.x to it.key.y }),
            //lasersHistory = visitedAcc.lasersHistory + (visitedAcc.lasersHistory + newLasers)
            lasersHistory = visitedAcc.lasersHistory + listOf(currentHistory)
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
