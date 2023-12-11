import java.awt.Polygon
import java.io.File
import kotlin.time.measureTime

fun main() {
    val input = getInput("input.txt")
    val inputTest = getInput("inputTest.txt")
    val inputTest2 = getInput("inputTest2.txt")
    val inputTest3 = getInput("inputTest3.txt")
    val inputTestPart2 = getInput("inputTestPart2.txt")
    val inputTest2Part2 = getInput("inputTest2Part2.txt")
    mainMeasuringTime({ solvePartTwo(inputTest2Part2) }, "InputTest2Part2")
    mainMeasuringTime({ solvePartTwo(inputTestPart2) }, "InputTestPart2")
    mainMeasuringTime({ solvePartTwo(input) }, "Input")
    mainMeasuringTime({ solvePartOne(inputTest) }, "InputTest")
    mainMeasuringTime({ solvePartOne(inputTest2) }, "InputTest2")
    mainMeasuringTime({ solvePartOne(inputTest3) }, "InputTest3")
    mainMeasuringTime({ solvePartOne(input) }, "Input")
}
main()

data class Input(val map: Map<Pos, Pipe>)
data class Pos(val x: Int, val y: Int)
enum class Pipe {
    V, H, NE, NW, SW, SE, G, Start
}

typealias CostMap = Map<Pos, Cost>

data class Cost(val cost: Long, val pipe: Pipe)
data class LoopPath(val path: List<Pair<Pos, Cost>>)

fun Pipe.toChar() = when (this) {
    Pipe.V -> '|'
    Pipe.H -> '-'
    Pipe.NE -> 'L'
    Pipe.NW -> 'J'
    Pipe.SW -> '7'
    Pipe.SE -> 'F'
    Pipe.G -> '.'
    Pipe.Start -> 'S'
}

fun Char.toPipe() = when (this) {
    '|' -> Pipe.V
    '-' -> Pipe.H
    'L' -> Pipe.NE
    'J' -> Pipe.NW
    '7' -> Pipe.SW
    'F' -> Pipe.SE
    '.' -> Pipe.G
    'S' -> Pipe.Start
    else -> throw Exception("No match to pipe $this")
}

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val map =
        file.split("\n").mapIndexed { y, line -> line.mapIndexed { x, value -> Pos(x, y) to value.toPipe() } }.flatten()
            .toMap()
    return Input(map)
}

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint $funTime")

}

fun solvePartTwo(input: Input): Long {
    val startPoint = input.map.entries.find { it.value == Pipe.Start }!!
    println("Start point is $startPoint")
    val loopPath = exploreAll(input, startPoint)
    val maxX = input.map.entries.maxOf { it.key.x }
    val maxY = input.map.entries.maxOf { it.key.y }
    val notOnLoopPoses = (0..maxY).map { y ->
        (0..maxX).map { x ->
            Pos(x, y)
        }
    }.flatten().filter { pos -> pos !in loopPath.path.map { it.first } }
    val withinTheLoopMap = buildWithinLoopMap(
        notOnLoopPoses,
        loopPath
    )
    debug(withinTheLoopMap, loopPath, maxX, maxY)
    return withinTheLoopMap.count { it.value }.toLong()
}

fun debug(withinTheLoopMap: Map<Pos, Boolean>, loopPath: LoopPath, maxX: Int, maxY: Int) {
    doPrint("WithingTheLoopMap", loopPath, maxX, maxY) { pos, match ->
        if (pos in withinTheLoopMap.keys) {
            print("I")
        } else if (match != null && match.second.pipe != Pipe.G) {
            print(match.second.pipe.toChar())
        } else print(".")
    }
    printLoop(loopPath, maxX, maxY)
}

fun printLoop(loopPath: LoopPath, maxX: Int, maxY: Int) {
    doPrint("LoopPath", loopPath, maxX, maxY) { _, match ->
        if (match != null && match.second.pipe != Pipe.G) {
            print(match.second.pipe.toChar())
        } else print(".")
    }
}

fun doPrint(toPrint: String, loopPath: LoopPath, maxX: Int, maxY: Int, fn: (Pos, Pair<Pos, Cost>?) -> Unit) {
    println()
    println(toPrint)
    (0..maxY).map { y ->
        (0..maxX).map { x ->
            val pos = Pos(x, y)
            val match: Pair<Pos, Cost>? = loopPath.path.find { it.first == pos }
            fn(pos, match)
        }
    }
    println()
}


fun solvePartOne(input: Input): Long {
    val startPoint = input.map.entries.find { it.value == Pipe.Start }!!
    println("Start point is $startPoint")
    val results = exploreAll(input, startPoint)
    val result = results.path.maxOf { it.second.cost }
    return result
}

fun exploreAll(input: Input, startPoint: Map.Entry<Pos, Pipe>): LoopPath {
    val rightPos = startPoint.key.right()
    val leftPos = startPoint.key.left()
    val topPos = startPoint.key.top()
    val bottomPos = startPoint.key.bottom()

    val toExplore = listOfNotNull(
        input.map[rightPos]?.let { rightPos to it },
        input.map[leftPos]?.let { leftPos to it },
        input.map[topPos]?.let { topPos to it },
        input.map[bottomPos]?.let { bottomPos to it }
    )
    val initCostMap = mapOf(startPoint.key to Cost(0, startPoint.value))
    val results = toExplore.map { point ->
        explore(
            point,
            input.map, Cost(1, point.second), initCostMap + mapOf(point.first to Cost(1, point.second)), startPoint.key
        )
    }
    val resultFiltered = results.map { costMap ->
        costMap.filter { isLowestCost(results, costMap, it.key, it.value) }
    }
    val resultsSortedByMaxCost =
        resultFiltered.sortedBy { it.values.maxOfOrNull { value -> value.cost } ?: 0 }.reversed()
            .map { map -> map.toList().sortedBy { it.second.cost }.reversed() }

    val path = resultsSortedByMaxCost[0].reversed().dropLast(1) + resultsSortedByMaxCost[1].dropLast(1)
    return LoopPath(path)
}


tailrec fun explore(
    point: Pair<Pos, Pipe>,
    map: Map<Pos, Pipe>,
    cost: Cost,
    costMap: CostMap = mapOf(),
    prevPos: Pos
): CostMap {
    val newPos: Pos? = point.newPos(prevPos)
    val pipe = map[newPos]
    val newPoint: Pair<Pos, Pipe>? = if (newPos != null && pipe != null && pipe != Pipe.G) newPos to pipe else null

    return if (newPos == null || newPoint == null) {
        mapOf()
    } else if (pipe == Pipe.Start) {
        costMap
    } else {
        val newCost = Cost(cost.cost + 1, newPoint.second)
        val maybeCostForNewPos = costMap[newPos]
        val newCostMap = if (maybeCostForNewPos == null) {
            costMap + mapOf(newPos to newCost)
        } else if (maybeCostForNewPos.cost <= newCost.cost) {
            costMap
        } else {
            costMap + mapOf(newPos to newCost)
        }
        explore(
            newPoint,
            map,
            newCost,
            newCostMap,
            prevPos = point.first
        )
    }
}

fun Pair<Pos, Pipe>.newPos(prevPos: Pos) = when (this.second) {
    Pipe.V -> if (wasOnTop(prevPos)) this.first.bottom() else if (wasOnBottom(prevPos)) this.first.top() else null
    Pipe.H -> if (wasOnTheLeft(prevPos)) this.first.right() else if (wasOnTheRight(prevPos)) this.first.left() else null
    Pipe.NE -> if (wasOnTop(prevPos)) this.first.right() else if (wasOnTheRight(prevPos)) this.first.top() else null
    Pipe.NW -> if (wasOnTop(prevPos)) this.first.left() else if (wasOnTheLeft(prevPos)) this.first.top() else null
    Pipe.SW -> if (wasOnTheLeft(prevPos)) this.first.bottom() else if (wasOnBottom(prevPos)) this.first.left() else null
    Pipe.SE -> if (wasOnBottom(prevPos)) this.first.right() else if (wasOnTheRight(prevPos)) this.first.bottom() else null
    Pipe.G -> this.first
    Pipe.Start -> this.first
}

fun Pos.left() = this.copy(x = this.x - 1)
fun Pos.right() = this.copy(x = this.x + 1)
fun Pos.top() = this.copy(y = this.y - 1)
fun Pos.bottom() = this.copy(y = this.y + 1)

fun Pair<Pos, Pipe>.wasOnTop(prevPos: Pos) = prevPos.y < this.first.y
fun Pair<Pos, Pipe>.wasOnBottom(prevPos: Pos) = prevPos.y > this.first.y
fun Pair<Pos, Pipe>.wasOnTheLeft(prevPos: Pos) = prevPos.x < this.first.x
fun Pair<Pos, Pipe>.wasOnTheRight(prevPos: Pos) = prevPos.x > this.first.x

fun buildWithinLoopMap(
    allPoses: List<Pos>,
    loopPath: LoopPath
): Map<Pos, Boolean> {
    val xPoints = loopPath.path.map { it.first.x }
    val yPoints = loopPath.path.map { it.first.y }
    val polygon = Polygon(xPoints.toIntArray(), yPoints.toIntArray(), xPoints.size)
    return allPoses.fold(mapOf()) { acc, pos ->
        if (polygon.contains(pos.x, pos.y)) {
            acc + mapOf(pos to true)
        } else acc
    }
}

fun isLowestCost(
    results: List<_10.CostMap>,
    costMap: _10.CostMap,
    pos: Pos,
    cost: Cost
): Boolean {
    val newResults = results.toMutableList()
    newResults.remove(costMap)
    return newResults.find { otherCostMap ->
        val otherCost = otherCostMap[pos]
        otherCost != null && otherCost.cost < cost.cost
    } == null
}