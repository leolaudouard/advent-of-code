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

data class Point(val heatLoss: Long)
data class Input(val list: List<List<Point>>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val lines = file.split("\n").map { char -> char.map { Point(it.digitToInt().toLong()) } }
    println(lines.joinToString("\n"))
    return Input(lines)
}

fun solve(input: Input): Number {
    val heatLossAcc = input.list[0][0].heatLoss
    val maxHeatLossAcc = input.list[0].sumOf { it.heatLoss } + input.list.sumOf { it.last().heatLoss }
    val states = listOf(
        State(0, 0, Direction.R, 0, heatLossAcc),
        State(0, 0, Direction.B, 0, heatLossAcc),
    )

    val min = findMinHeatLossToDestination(states, input, maxHeatLossAcc, setOf()).minBy { it.currentHeatLost }.currentHeatLost

    println("Result $min")
    return min
}

enum class Direction {
    T, B, L, R
}

tailrec fun findMinHeatLossToDestination(
    states: List<State>,
    input: Input,
    maxHeatLossAcc: Long,
    visited: Set<State>
): List<State> {
    val nextStates = states.flatMap { state -> state.nextStates(input) }
    val nextStatesFiltered = nextStates.filter { state ->
        val alreadySeen = visited.filter { visited ->
            visited.x == state.x && visited.y == state.y && visited.direction == state.direction
        }
        if (state.isTarget(input)) {
            return@filter true
        }
        if (state.currentHeatLost >= maxHeatLossAcc) return@filter false

        val alreadyVisitedWithLowerHeatLoss = alreadySeen.isNotEmpty()
                && alreadySeen.any { seen -> seen.currentHeatLost <= state.currentHeatLost }
        if (alreadyVisitedWithLowerHeatLoss) return@filter false

        true
    }
    if (nextStatesFiltered.isEmpty()) return listOf()
    return findMinHeatLossToDestination(nextStatesFiltered, input, maxHeatLossAcc, visited)
}

fun Set<State>.printAll(input: Input) {
    (0..input.list.size - 1).map { y ->
        println()
        (0..input.list.first().size - 1).map { x ->
            val match = this.find { it.x == x && it.y == y }
            if (match != null) {
                val char = when (match.direction) {
                    Direction.T -> "^"
                    Direction.B -> "v"
                    Direction.L -> "<"
                    Direction.R -> ">"
                }
                print(char)
            } else print(".")
        }
    }

}


fun State.isTarget(input: Input): Boolean = (this.x == input.list.first().size - 1 && this.y == input.list.size - 1)

data class Edge(val from: Node, val to: Node, val weight: Int)
data class Node(val currentBlock: Int, val currentHeatLoss: Int)

fun State.nextStates(input: Input): List<State> {
    val opposite = this.direction.opposite()
    val nextStates =
        Direction.entries.filter { it != opposite && it != this.direction }.mapNotNull { direction ->
            getNextStateOrNull(direction, this, input, incrementBlocksCount = direction == this.direction)
        }
    return if (this.currentBlocksCount == 3) nextStates.filter { it.direction != this.direction }
    else nextStates
}


data class State(
    val x: Int,
    val y: Int,
    val direction: Direction,
    val currentBlocksCount: Int,
    val currentHeatLost: Long
)

fun Direction.opposite(): Direction = when (this) {
    Direction.T -> Direction.B
    Direction.B -> Direction.T
    Direction.L -> Direction.R
    Direction.R -> Direction.L
}

fun doTravel(x: Int, y: Int, direction: Direction): Pair<Int, Int> {
    return when (direction) {
        Direction.T -> x to y - 1
        Direction.B -> x to y + 1
        Direction.L -> x - 1 to y
        Direction.R -> x + 1 to y
    }
}

fun deadEnd(x: Int, y: Int, direction: Direction, input: Input): Boolean {
    return (direction == Direction.T && y == 0)
            ||
            (direction == Direction.B && y == input.list.size - 1)
            ||
            (direction == Direction.L && x == 0)
            ||
            (direction == Direction.R && x == input.list.first().size - 1)
            || x < 0 || x >= input.list.first().size || y < 0 || y >= input.list.size

}

fun getNextStateOrNull(direction: Direction, state: State, input: Input, incrementBlocksCount: Boolean): State? {
    val (newX, newY) = doTravel(state.x, state.y, direction)
    val nextHeatLoss = input.list.getOrNull(newY)?.getOrNull(newX)?.heatLoss ?: return null
    val newBlockCount = if (incrementBlocksCount) state.currentBlocksCount + 1 else state.currentBlocksCount
    return State(
        newX,
        newY,
        direction,
        currentBlocksCount = newBlockCount,
        currentHeatLost = state.currentHeatLost + nextHeatLoss
    )
}