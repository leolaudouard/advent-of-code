import _16.Side.*
import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import kotlin.math.max
import kotlin.system.exitProcess
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

data class Visited(val lasersHistory: List<Key>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val lines = file.split("\n")
    return Input(lines)
}

fun solve(input: Input): Number {
    val maxX = input.list.first().length
    val maxY = input.list.size
    val queue = LinkedBlockingQueue<Int>()
    val lasers = listOf(Key(-1, 0, R))
    getThat(lasers, input, maxX, maxY, queue).join()
    val size = queue.max()
    println("Part 1 $size")
    val threads = (0..maxY).map { y ->
        val lasers = listOf(Key(-1, y, R), Key(maxX + 1, y, L))
        getThat(lasers, input, maxX, maxY, queue)
    }
    val moreThreads = (0..maxX).map { x ->
        val lasers = listOf(Key(x, -1, B), Key(x, maxY + 1, T))
        getThat(lasers, input, maxX, maxY, queue)
    }

    (threads + moreThreads).map { it.join() }
    return queue.max()
}

fun getThat(lasers: List<Key>, input: Input, maxX: Int, maxY: Int, queue: LinkedBlockingQueue<Int>): Thread {
    val thread = Thread(
    ) {
        val visited = Visited(lasers)
        val visitedPoses = recurse(lasers, input, visited, maxX, maxY)
        val size = visitedPoses.lasersHistory.size(maxX, maxY)
        //println(size)
        queue.add(size)
    }
    thread.start()
    return thread
}

tailrec fun recurse(lasers: List<Key>, input: Input, visited: Visited, maxX: Int, maxY: Int): Visited {
    val (newLasers, newVisited) = travel(lasers, input.list, visited, maxX, maxY)
    //newVisited.lasersHistory.print(input.list.first().length, input.list.size)
    //val size = newVisited.lasersHistory.size(input.list.first().length, input.list.size)
    //println("Size: $size, Lasers count: ${newLasers.size}")
    return if (newVisited.lasersHistory.distinct() == visited.lasersHistory.distinct()) {
        newVisited
    } else recurse(
        newLasers,
        input,
        newVisited.copy(lasersHistory = newVisited.lasersHistory),
        maxX, maxY
    )
}

fun travel(lasers: List<Key>, lines: List<String>, visited: Visited, maxX: Int, maxY: Int): Pair<List<Key>, Visited> {
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
            lasersHistory = currentHistory.distinct()
        )
        (lasersAcc + newLasers.filter { it !in visited.lasersHistory && it.x in (-1..maxX + 1) && it.y in (-1..maxY + 1) }) to newVisited
    }.let { (lasers, visited) ->
        lasers to visited
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
    return this.map { it.x to it.y }.distinct().filter { (x, y) ->
        x in 0..maxX && y in 0..maxY
    }.size
}

fun List<Key>.sorted(): Any {
    return this.sortedBy {  it.x}.sortedBy { it.y }.sortedBy { it.side }
}
