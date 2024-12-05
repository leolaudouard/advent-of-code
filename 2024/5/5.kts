import java.io.File
import kotlin.time.measureTime
import kotlin.math.round

fun main() {
    val inputTest = getInput("inputTest.txt")
    val input = getInput("input.txt")
    println("-------- PART 1 --------")
    println(inputTest)
    mainMeasuringTime({ part1(inputTest) }, "InputTest")
    mainMeasuringTime({ part1(input) }, "Input")
    println("\n-------- PART 2 --------")
    mainMeasuringTime({ part2(inputTest) }, "InputTest")
    mainMeasuringTime({ part2(input) }, "Input")
}
main()

data class Input(val rules: List<Pair<Int, Int>>, val updates: List<List<Int>>)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val splitted = file.split("\n").dropLast(1)
    val rules = splitted.takeWhile { it != "" }
    val updates = splitted.dropWhile { it != "" }.drop(1)

    return Input(rules = rules.map {
        val rule = it.split("|").map { num -> num.toInt() }
        rule[0] to rule[1]
    }, updates = updates.map { it.split(",").map { num -> num.toInt() } })
}

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint exec time $funTime")

}

fun part1(input: Input): Long {
    return input.updates.map { update ->
        if (isValid(update, input.rules)) {
            middle(update)
        } else 0
    }.sum().toLong()
}

private fun isValid(update: List<Int>, rules: List<Pair<Int, Int>>): Boolean {
    return update.withIndex().all { (index, value) ->
        val numbersThatMustBeBefore = rules.filter { (_, after) -> after == value }.map { (before, _) -> before }
        val numbersThatMustBeAfter = rules.filter { (before, _) -> before == value }.map { (_, after) -> after }
        val before: List<Int> = update.subList(0, index)
        val after: List<Int> = update.subList(index + 1, update.size)
        numbersThatMustBeBefore.all { it !in after } and numbersThatMustBeAfter.all { it !in before }
    }
}

private fun middle(update: List<Int>): Int {
    val divided = update.size.toDouble() / 2
    val rounded = divided.toInt()
    return update.get(rounded)
}

fun part2(input: Input): Long {
    return input.updates.map { update ->
        sortWithRules(update, input.rules)
        .let { sorted -> middle(sorted)}
    }.sum().toLong()
}

private fun sortWithRules(update: List<Int>, rules: List<Pair<Int, Int>>): List<Int> {
    if (isValid(update, rules)) return listOf(0)

    return update.sortedWith { first, second ->
        when {
            rules.filter {(_, after) -> after == first }.map {(before, _) -> before} .contains(second) -> -1
            rules.filter {(_, after) -> after == second }.map{(before, _) -> before}.contains(first) -> 1
            else -> 0
        }
    }
}
