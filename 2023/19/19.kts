import java.io.File
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

data class Input(val graph: Set<Edge>, val initialStates: List<State>)
data class Edge(val source: Node, val destinations: List<Destination>)
data class Node(val name: String)
data class Destination(val node: Node, val conditionFun: (State) -> Pair<Boolean, Long>, val condition: Condition)
data class Condition(val value: Long, val op: Char, val appliesTo: Char)
data class State(val x: Int, val m: Int, val a: Int, val s: Int)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val lines = file.split("\n\n")
    val graphLines = lines[0].split("\n")
    val edges = graphLines.map { line ->
        val source = Node(line.substringBefore("{"))
        val rest = line.substringAfter("{").substringBefore("}")
        val destinations = getDestinations(rest)
        Edge(source, destinations)
    }
    val initialStates = lines[1].split("\n").map { line ->
        val x = line.substringAfter("x=").substringBefore(",").toInt()
        val m = line.substringAfter("m=").substringBefore(",").toInt()
        val a = line.substringAfter("a=").substringBefore(",").toInt()
        val s = line.substringAfter("s=").substringBefore("}").toInt()
        State(x, m, a, s)
    }

    return Input(edges.toSet(), initialStates)
}

fun compare(value: Int, op: Char, otherValue: Long) = when (op) {
    '>' -> value > otherValue
    '<' -> value < otherValue
    else -> throw Exception("Unknown operand $op")
}

fun solve(input: Input): Number {
    val startNode = input.graph.find { it.source.name == "in" } ?: throw Exception("No start point.")
    return input.initialStates.sumOf {
        doSolve(startNode, input, it)
    }
}

tailrec fun doSolve(start: Edge, input: Input, state: State): Int {
    val next = start.destinations.find { destination ->
        destination.conditionFun(state).first
    } ?: throw Exception("Dead end")

    if (next.node.name == "A") return state.a + state.m + state.s + state.x
    if (next.node.name == "R") return 0
    val newEdge = input.graph.find { it.source == next.node } ?: throw Exception("Not found")
    return doSolve(newEdge, input, state)
}

fun getDestinations(rest: String) = rest.split(",").map { str ->
    val first = str.first()
    val destinationNode = Node(str.substringAfter(":"))
    if (!str.contains(":")) return@map Destination(
        Node(str),
        { true to 0 },
        Condition(0, '>', appliesTo = 'x')
    )
    val op = str.drop(1).first()
    val value = str.dropWhile { it.digitToIntOrNull() == null }.substringBefore(":").toLongOrNull()
        ?: throw Exception("Error parsing str value $str")
    val condition = { state: State ->
        when (first) {
            'x' -> compare(state.x, op, value) to value
            'm' -> compare(state.m, op, value) to value
            'a' -> compare(state.a, op, value) to value
            's' -> compare(state.s, op, value) to value
            else -> throw Exception("Something is wrong $first")
        }
    }
    Destination(destinationNode, condition, Condition(value, op, first))
}