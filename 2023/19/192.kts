import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTime

fun main() {
    val inputTest = getInput("inputTest.txt")
    val input = getInput("input.txt")
    mainMeasuringTime({ solve(inputTest) }, "InputTest")
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

data class Input(val graph: Map<Node, Destination>)
data class Node(val name: String)
data class Destination(val rules: List<Rule>, val fallback: Node)
data class Rule(val node: Node, val condition: Condition)
data class Condition(val value: Long, val op: Char, val appliesTo: Char)


fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val lines = file.split("\n\n").first().split("\n")
    val edges = lines.associate { line ->
        val node = Node(line.substringBefore("{"))
        val rest = line.substringAfter("{").substringBefore("}").split(",")
        val fallback = Node(rest.last())
        val rules = rest.dropLast(1).map { str ->
            val first = str.first()
            val destinationNode = Node(str.substringAfter(":"))
            val op = str.drop(1).first()
            val value = str.dropWhile { it.digitToIntOrNull() == null }.substringBefore(":").toLongOrNull()
                ?: throw Exception("Error parsing str value $str")


            Rule(destinationNode, Condition(value, op, first))
        }
        node to Destination(rules, fallback)
    }
    return Input(edges)
}

fun solve(input: Input): Long {
    println("Here is your input\n")
    println(input.graph.entries.joinToString("\n"))
    val ranges = ("xmas").map { it to Constraint(1, 4000) }.toMap()
    return doSolve(ranges, input.graph)
}

data class Constraint(val low: Long, val high: Long)

fun doSolve(
    ranges: Map<Char, Constraint>,
    graph: Map<Node, Destination>,
    node: Node = Node("in")
): Long {
    if (node.name == "R") {
        return 0
    }
    if (node.name == "A") {
        print("Found A: ")
        print(ranges.map { (char, constraint) -> constraint.low.toString() + " " + char + " " + constraint.high.toString() }
            .joinToString("; "))
        println()
        return ranges.values.possibilities()
    }
    val destinations = graph[node]!!
    var total = 0L

    val failingRanges: MutableMap<Char, Constraint> = ranges.toMutableMap()
    destinations.rules.takeWhile { rule: Rule ->
        val appliesTo = rule.condition.appliesTo
        val constraint = failingRanges[appliesTo]!!
        val (pass, fail) = getNextConstraints(rule.condition, constraint)

        if (pass.low < pass.high) {
            val rangesCopy = failingRanges.toMap()
            val newRangesForPass = rangesCopy + mapOf(appliesTo to pass)
            total += doSolve(newRangesForPass, graph, rule.node)
        }

        if (fail.low < fail.high) {
            failingRanges[appliesTo] = fail
            true
        } else {
            // Break the loop, the "failing" constraint search reached an impossible constraint.
            false
        }
    }
    if (failingRanges.values.all { it.low < it.high }) {
        total += doSolve(failingRanges, graph, destinations.fallback)
    }
    return total
}

fun Collection<Constraint>.possibilities() =
    this.fold(1L) { acc, constraint -> acc * (constraint.high - constraint.low + 1) }

fun getNextConstraints(condition: Condition, constraint: Constraint) = when (condition.op) {
    '<' -> {
        val passConstraint = Constraint(constraint.low, min(constraint.high, condition.value - 1))
        val failConstraint = Constraint(max(condition.value, constraint.low), constraint.high)
        passConstraint to failConstraint
    }

    '>' -> {
        val passConstraint = Constraint(max(constraint.low, condition.value + 1), constraint.high)
        val failConstraint = Constraint(constraint.low, min(constraint.high, condition.value))
        passConstraint to failConstraint
    }

    else -> throw Exception("Unknown op ${condition.op}")
}
