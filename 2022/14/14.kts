import _2.Symbol.*
import java.io.File
import kotlin.math.abs

val input = File("input.txt").readText()
val lines = input.split('\n').dropLast(1)

class Grid {
    private val state: MutableMap<Pos, Symbol> = hashMapOf()

    fun get() = state
    fun put(pos: Pos, symbol: Symbol) {
        when (state.get(pos)) {
            SENSOR,
            BEACON -> Unit

            else -> state[pos] = symbol
        }
    }

}

val grid = Grid()
val couples = lines.map {
    val couple = getCouple(it)
    println(couple)
    grid.put(couple.beaconPos, BEACON)
    grid.put(couple.sensorPos, SENSOR)
    couple
}
couples.map {
    putStars(it)
}

fun putStars(couple: Couple) {
    val distance = distance(couple.sensorPos, couple.beaconPos)
    val ref = couple.sensorPos
    //thatFun(ref, ref.copy(),distance)
    putTopLeftStars(ref, ref.copy(),distance)
    putTopRightStars(ref, ref.copy(),distance)
    putBottomLeftStars(ref, ref.copy(),distance)
    putBottomRightStars(ref, ref.copy(),distance)
}

tailrec fun putBottomRightStars(ref: Pos, pos: Pos, distance: Int) {
    if (predicate(ref, pos, distance)) {
        grid.put(pos, STAR)
        putBottomRightStars(ref, pos.copy(x = pos.x + 1), distance)
        putBottomRightStars(ref, pos.copy(y = pos.y + 1), distance)
    }
}
tailrec fun putTopLeftStars(ref: Pos, pos: Pos, distance: Int) {
    if (predicate(ref, pos, distance)) {
        grid.put(pos, STAR)
        putTopLeftStars(ref, pos.copy(x = pos.x - 1), distance)
        putTopLeftStars(ref, pos.copy(y = pos.y - 1), distance)
    }
}
tailrec fun putTopRightStars(ref: Pos, pos: Pos, distance: Int) {
    if (predicate(ref, pos, distance)) {
        grid.put(pos, STAR)
        putTopRightStars(ref, pos.copy(x = pos.x - 1), distance)
        putTopRightStars(ref, pos.copy(y = pos.y + 1), distance)
    }
}
tailrec fun putBottomLeftStars(ref: Pos, pos: Pos, distance: Int) {
    if (predicate(ref, pos, distance)) {
        grid.put(pos, STAR)
        putBottomLeftStars(ref, pos.copy(x = pos.x + 1), distance)
        putBottomLeftStars(ref, pos.copy(y = pos.y - 1), distance)
    }
}

val xRange = (-15..50)
val yRange = (-15..50)

fun distance(pos: Pos, secondPos: Pos): Int = abs(pos.x - secondPos.x) + abs(pos.y - secondPos.y)



data class Pos(val x: Int, val y: Int)

data class Couple(val sensorPos: Pos, val beaconPos: Pos)

data class Todo(val couples: List<Couple>)

enum class Symbol {
    SENSOR, BEACON, STAR, UNDEFINED
}
typealias Grid = List<Pair<Pos, Symbol>>

fun getCouple(str: String): Couple {
    val sensorPos = getSensorPos(str)
    val beaconPos = getBeaconPos(str)
    return Couple(sensorPos, beaconPos)
}

fun getBeaconPos(str: String): Pos = str.substringAfter("y=").let {
    Pos(toInt(it, "x="), toInt(it, "y="))
}

fun getSensorPos(str: String): Pos = Pos(toInt(str, "x="), toInt(str, "y="))

fun toInt(str: String, delimiter: String): Int {
    val subString = str.substringAfter(delimiter)
    val isNegative = subString.first() == '-'
    val value = subString.map { it.digitToIntOrNull() }
        .asSequence()
        .dropWhile { it == null }
        .takeWhile { it != null }
        .filterNotNull()
        .map { it.toString() }
        .joinToString("")
        .toInt()
    return if (isNegative) {
        -value
    } else value
}

fun displayGrid() = xRange
    .forEach { x ->
        grid.get()
            .entries
            .filter { it.key.x == x }
            .let { line ->
                //println("Match $x ${line.map { it.key }}")
                yRange.forEach { y ->
                    line.find { it.key.y == y }
                        ?.let {
                            when (it.value) {
                                SENSOR -> print("S")
                                BEACON -> print("B")
                                STAR -> print("#")
                                UNDEFINED -> print(".")
                            }
                        } ?: print(".")
                }
                println()
            }
    }


println(grid.get().entries.filter { it.value == SENSOR || it.value == BEACON })
//displayGrid()
val yTest = 10
val yPartOne = 2000000

val result = grid.get()
    .filter { it.key.y == yPartOne && it.value == Symbol.STAR}
    .count()

println(result)

fun predicate(ref: Pos, pos: Pos, distance: Int) = distance(ref, pos) <= distance