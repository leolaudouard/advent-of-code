import java.io.File
import kotlin.math.abs
import kotlin.system.exitProcess

val inputTest = File("inputTest.txt").readText()
val input = File("input.txt").readText()
val linesTest = inputTest.split('\n')
val lines = input.split('\n')


fun main() {
    val testResult = solve(linesTest)
    println("TestResult $testResult")
    val result = solve(lines)
    println("result $result")
}
main()


data class ThatNumber(val int: Int, val x: Int, val y: Int, val length: Int)
data class Pos(val x: Int, val y: Int)

fun solve(lines: List<String>): Int {
    val numbers: List<ThatNumber> = lines.mapIndexed { index, line ->
        parse(line, index)
    }.flatten()
    val numberWithAdjacentStars = numbers
        .map {
            number ->
            val symbolPoses = getStarPoses(number, lines)
            number to symbolPoses
        }.filter { (number, symbolPoses) ->
            symbolPoses.isNotEmpty()
        }.let {
            println(it.joinToString("\n"))
            it
        }
    println("-----")
        return numberWithAdjacentStars.foldIndexed(listOf<Gear>()) { index, acc, (number, poses) ->
            val subList = numberWithAdjacentStars.subList(index + 1, numberWithAdjacentStars.size)
            println(subList)
            val match = subList.find { (otherNumber, otherPoses) ->
                otherPoses.any { otherPose -> poses.any { pose -> pose.x == otherPose.x && pose.y == otherPose.y }  }
            }
            if (match != null) {
                println("Match $number, $poses ${match.first}, ${match.second}")
               acc + Gear(number, match.first, match.second)
            } else acc
        }
        .let {
            println(it.joinToString("\n"))
            it
        }.sumOf { gear -> gear.first.int * gear.second.int }
}

data class Gear(val first: ThatNumber, val second: ThatNumber, val starPos: List<Pos>)

fun getStarPoses(number: ThatNumber, lines: List<String>): List<Pos> {
    return (0..<number.length)
        .map { toAdd ->
            listOf(
                getStarPosOrNull(lines, number, toAdd, 1),
                getStarPosOrNull(lines, number, toAdd, -1),
            )
        }.flatten().filterNotNull() + (listOfNotNull(
        getStarPosOrNull(lines, number, toAddy = -1, toAddX = 0),
        getStarPosOrNull(lines, number, toAddy = 1, toAddX = 0),
        getStarPosOrNull(lines, number, toAddy = number.length, toAddX = 0),
        getStarPosOrNull(lines, number, toAddy = number.length, toAddX = 1),
        getStarPosOrNull(lines, number, toAddy = number.length, toAddX = -1),
        getStarPosOrNull(lines, number, toAddy = -1, toAddX = 0),
        getStarPosOrNull(lines, number, toAddy = -1, toAddX = 1),
        getStarPosOrNull(lines, number, toAddy = -1, toAddX = -1)
    ))
}

fun parse(line: String, x: Int): List<ThatNumber> {
    val ok = line.foldIndexed(listOf<ThatNumber>()) { index, acc, char ->
        when (val int = char.digitToIntOrNull()) {
            null -> acc
            else -> when (getIntOrNull(line, index - 1)) {
                null -> (1..5).map { toAdd ->
                    getIntOrNull(line, index + toAdd)
                }.takeWhile { it != null }
                    .let { ints ->
                        val list: List<String> = listOf(int.toString()) + ints.map { it.toString() }
                        val str = list.joinToString("")
                        acc + ThatNumber(str.toInt(), x, index, str.length)
                    }

                else -> acc
            }
        }
    }
    return ok
}

fun getIntOrNull(line: String, index: Int) = line.getOrNull(index)?.digitToIntOrNull()

fun getStarPosOrNull(lines: List<String>, number: ThatNumber, toAddy: Int, toAddX: Int): Pos? {
    val x = number.x + toAddX
    val y = number.y + toAddy
    return if (lines.getOrNull(x)?.getOrNull(y).isStar()) {
        Pos(x, y)
    } else null
}

fun Char?.isSymbol() = this != '.' && this?.digitToIntOrNull() == null && this != null
fun Char?.isStar() = this == '*'
