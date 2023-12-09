import java.io.File
import kotlin.math.abs

val inputTest = File("inputTest.txt").readText()
val input = File("input.txt").readText()
val linesTest = inputTest.split('\n')
val lines = input.split('\n')

fun main() {
    val testResult = solvePartOne(linesTest, Tirage(red = 12, green = 13, blue = 14))
    val result = solvePartOne(lines, Tirage(red = 12 , green = 13 , blue = 14 ))
    println("TestResult $testResult")
    println("result $result")

    val testResultPartTwo = solvePartTwo(linesTest)
    val resultPartTwo = solvePartTwo(lines)
    println("TestResult partTwo $testResultPartTwo")
    println("result partTwo $resultPartTwo")
}
main()


data class Tirage(val blue: Int, val red: Int, val green: Int)
data class Game(val id: Int, val tirages: Set<Tirage>)

fun solvePartOne(lines: List<String>, tirageRef: Tirage): Int {
    val games = parseGames(lines)
    return games.filter { game ->
        game.tirages.all { tirage ->
            tirage.green <=  tirageRef.green && tirage.red <= tirageRef.red && tirage.blue <= tirageRef.blue
        }
    }.sumOf { it.id }
}

fun solvePartTwo(lines: List<String>): Int {
    val games = parseGames(lines)
    val minTirages = games.map { game ->
        val minRed = game.tirages.maxBy { tirage ->
            tirage.red
        }.red
        val minGreen = game.tirages.maxBy { tirage ->
            tirage.green
        }.green
        val minBlue = game.tirages.maxBy { tirage ->
            tirage.blue
        }.blue
        Tirage(
            red = minRed,
            blue = minBlue,
            green = minGreen,
        )
    }
    return minTirages.sumOf {
        it.blue  * it.red * it.green
    }
}

fun parseGames(lines: List<String>): List<Game>   = lines.map {
    parseGame(it)
}

fun parseGame(line: String): Game {
    val subtring = line.substringAfter("Game ")
    val gameId = subtring.substringBefore(":").toInt()
    val sets = subtring.substringAfter(":").split(";").map(::parseTirage).toSet()
    return Game(gameId, sets)
}

fun parseTirage(tirage: String): Tirage {
    val blue = parseColor(tirage, "blue")
    val green = parseColor(tirage, "green")
    val red = parseColor(tirage, "red")
    return Tirage(
        blue = blue, red = red, green = green
    )
}

fun parseColor(tirage: String, color: String): Int {
    return if (color in tirage) {
        val substring = tirage.substringBefore(" $color").substringAfterLast(",")
        substring.replace(" ", "").toInt()
    } else 0
}