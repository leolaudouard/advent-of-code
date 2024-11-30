import java.io.File
import java.util.PriorityQueue
import kotlin.time.measureTime

fun main() {
    listOf("inputTest.txt", "inputTest2.txt", "input.txt").forEach { file ->
        val input = getInput(file)
        mainMeasuringTime({ solve(input) }, file)
    }
}
main()


fun mainMeasuringTime(someFun: () -> Number, toPrint: String) {
    val time = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint $time")
}

data class Point(val heatLoss: Int)
data class Input(val list: List<List<Point>>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val lines = file.split("\n").map { char -> char.map { Point(it.digitToInt()) } }
    return Input(lines)
}

fun solve(input: Input): Number {
    val heatLoss = 0
    val stateList = listOf(
        State(0, 0, Direction(1, 0), 0, heatLoss),
        State(0, 0, Direction(0, 1), 0, heatLoss),
    )

    val statesQueue = PriorityQueue<State> { state, state2 ->
        aStarCompare(state, input, state2)
    }

    statesQueue.addAll(stateList)
    return findShortestHeatLoss(statesQueue, input, mutableMapOf())
}

data class SeenKey(val x: Int, val y: Int, val direction: Direction, val blocks: Int)

tailrec fun findShortestHeatLoss(
    stateQueue: PriorityQueue<State>,
    input: Input,
    seenStatesToHeatLoss: MutableMap<SeenKey, Int>,
): Int {
    val state = stateQueue.poll()
    val key = SeenKey(state.x, state.y, state.direction, state.blocks)
    if (seenBetter(state, seenStatesToHeatLoss, key)) return findShortestHeatLoss(
        stateQueue,
        input,
        seenStatesToHeatLoss
    )
    if (state.isTarget(input)) return state.heatLoss

    val nextStates = state.nextStates(input)
    stateQueue.addAll(nextStates)
    seenStatesToHeatLoss[key] = state.heatLoss
    return findShortestHeatLoss(stateQueue, input, seenStatesToHeatLoss)
}

fun seenBetter(state: State, seenStatesToHeatLoss: Map<SeenKey, Int>, key: SeenKey): Boolean {
    val seenHeatLoss = seenStatesToHeatLoss[key]
    return (seenHeatLoss != null && seenHeatLoss < state.heatLoss)
}

fun directions(): Set<Direction> = setOf(
    Direction(1, 0),
    Direction(-1, 0),
    Direction(0, 1),
    Direction(0, -1),
)

fun State.isTarget(input: Input): Boolean =
    (this.x == input.list.first().size - 1 && this.y == input.list.size - 1) && this.blocks >= 3

fun State.nextStates(input: Input): List<State> {
    val opposite = Direction(this.direction.dx * -1, this.direction.dy * -1)
    val forbiddenDirections = if (this.blocks == 9) setOf(opposite, this.direction) else setOf(opposite)
    if (this.blocks > 2) {
        val nextStates =
            (directions() - forbiddenDirections).mapNotNull { direction ->
                getNextStateOrNull(direction, this, input)
            }
        return nextStates
    } else {
        val offset = 3 - this.blocks
        val newHeatLoss = heatLossWithOffset(offset, x, y, this.direction, input) ?: return listOf()
        val newX = this.x + this.direction.dx * offset
        val newY = this.y + this.direction.dy * offset

        val nextState =
            State(newX, newY, this.direction, this.blocks + offset, this.heatLoss + newHeatLoss)
        return listOf(nextState)
    }
}

fun heatLossWithOffset(offSet: Int, x: Int, y: Int, direction: Direction, input: Input): Int? {
    val list = (1..offSet).map { i ->
        heatLoss(x + i * direction.dx, y + i * direction.dy, input)
    }
    if (null in list) return null
    return list.filterNotNull().sum()
}

fun heatLoss(x: Int, y: Int, input: Input): Int? = input.list.getOrNull(y)?.getOrNull(x)?.heatLoss

data class State(
    val x: Int,
    val y: Int,
    val direction: Direction,
    val blocks: Int,
    val heatLoss: Int
)

data class Direction(val dx: Int, val dy: Int)

fun getNextStateOrNull(
    direction: Direction,
    state: State,
    input: Input,
): State? {
    val (dx, dy) = direction
    val x = state.x + dx
    val y = state.y + dy
    val heatLoss = heatLoss(x, y, input) ?: return null
    val blocks = if (direction == state.direction) state.blocks + 1 else 0
    return State(x, y, direction, blocks = blocks, heatLoss = state.heatLoss + heatLoss)
}

fun aStarCompare(state: State, input: Input, state2: State) =
    (cost(state, input)).compareTo(cost(state2, input))

fun cost(state: State, input: Input): Int = state.heatLoss + heuristic(state, input)

fun heuristic(state: State, input: Input): Int =
    (input.list.first().size - 1 - state.x) + (input.list.size - 1 - state.y)

