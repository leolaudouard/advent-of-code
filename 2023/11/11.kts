import java.io.File
import java.util.*
import kotlin.math.absoluteValue
import kotlin.time.measureTime

fun main() {
    val inputTest = getInput("inputTest.txt")
    mainMeasuringTime({ solve(inputTest) }, "InputTest")
    val input = getInput("input.txt")
    mainMeasuringTime({ solve(input) }, "Input")
}
main()

data class Input(val lines: List<Point>)
sealed interface Point {
    val pos: Pos
}

data class Galaxy(override val pos: Pos, val id: UUID) : Point
data class EmptySpace(override val pos: Pos) : Point
data class Pos(val x: Long, val y: Long)
data class Universe(val galaxies: Set<Galaxy>, val emptySpaces: Set<EmptySpace>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val splitted = file.split("\n")
    val horizontalDuplicatedYList: List<IndexedValue<String>> = splitted.withIndex().filter { line ->
        line.value.all { it == '.' }
    }

    val verticalDuplicatedXList: List<IndexedValue<Char>> = splitted.first().withIndex().filter { x ->
        splitted.withIndex().all { y ->
            val line = splitted.getOrNull(y.index) ?: throw Exception("Y not found $y")
            val value = line.getOrNull(x.index) ?: throw Exception("X not found $x for $y")
            value == '.'
        }
    }
    val newList = splitted.toMutableList()
    horizontalDuplicatedYList.reversed().map { toAdd ->
        newList.add(toAdd.index + 1, toAdd.value)
    }

    val linesWithDuplicate = newList.map { line ->
        line.flatMapIndexed { xIndex, char ->
            if (xIndex in verticalDuplicatedXList.map { it.index }) {
                listOf(char, char)
            } else listOf(char)
        }
    }

    println("Horizontal duplicate: ${horizontalDuplicatedYList.size}")
    println("Vertical duplicate: ${verticalDuplicatedXList.size}")
    println("Max Y is ${linesWithDuplicate.size}")
    println("Max X is ${linesWithDuplicate.first().size}")


    val lineParsed = linesWithDuplicate.mapIndexed { y, line ->
        line.mapIndexed { x, char ->
            val pos = Pos(x = x.toLong(), y = y.toLong())
            if (char == '.') EmptySpace(pos) else if (char == '#') Galaxy(
                pos,
                UUID.randomUUID()
            ) else throw Exception("Parsing issue; Not a galaxy nor an empty space $char $x $y")
        }
    }.flatten()
    return Input(lineParsed)
}

fun printInput(input: Input) {
    val maxX = input.lines.maxBy { it.pos.x }.pos.x
    val maxY = input.lines.maxBy { it.pos.y }.pos.y
    println("Input\n")
    (0..maxY).map { y ->
        (0..maxX).map { x ->
            val pos = Pos(x = x, y = y)
            when (input.lines.find { it.pos == pos }) {
                is EmptySpace -> print(".")
                is Galaxy -> print("G")
                else -> throw Exception("Nope")

            }
        }
        println()
    }
}

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint $funTime")

}

data class GalaxyPair(val first: Galaxy, val second: Galaxy, val cost: Long)

fun solve(input: Input): Long {
    val galaxies = input.lines.filterIsInstance<Galaxy>()
    val pairsSet = HashSet<Pair<Galaxy, Galaxy>>()

    for (i in 0 until galaxies.size - 1) {
        for (j in i + 1 until galaxies.size) {
            val pair = Pair(galaxies[i], galaxies[j])
            val reversePair = Pair(galaxies[j], galaxies[i])
            if (!pairsSet.contains(pair) && !pairsSet.contains(reversePair)) {
                pairsSet.add(pair)
            }
        }
    }
    val galaxyPairs = pairsSet.map { (galaxy, otherGalaxy) ->
        GalaxyPair(galaxy, otherGalaxy, galaxy.distance(otherGalaxy))
    }
    println("Galaxies count ${galaxies.size}")
    println("Pair count ${galaxyPairs.size}")
    printInput(input)
    return galaxyPairs.sumOf {
        it.cost
    }
}

fun GalaxyPair.notAlreadyInList(galaxyPairs: List<GalaxyPair>) = galaxyPairs.find { pair ->
    pair.first.id in listOf(this.first.id, this.second.id) && pair.second.id in listOf(this.first.id, this.second.id)
} == null

fun Galaxy.distance(galaxy: Galaxy): Long {
    val xDistance = (this.pos.x - galaxy.pos.x).absoluteValue
    val yDistance = (this.pos.y - galaxy.pos.y).absoluteValue
    return xDistance + yDistance
}