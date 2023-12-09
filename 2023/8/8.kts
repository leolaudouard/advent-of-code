import java.io.File
import kotlin.time.measureTime


fun main() {
    val input = getInput("input.txt")
    val inputTest = getInput("inputTestNoLcm.txt")
    mainMeasuringTime({ solveLcm(inputTest) }, "Lcm InputTest")
    mainMeasuringTime({ solveBruteForce(inputTest) }, "BrutForce InputTest")
    mainMeasuringTime({ solveGoodOne(inputTest) }, "GoodOne InputTest")

    mainMeasuringTime({ solveLcm(input) }, "Lcm Input")
    mainMeasuringTime({ solveGoodOne(input) }, "GoodOne Input")
    mainMeasuringTime({ solveBruteForce(input) }, "BrutForce Input")
}
main()

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val splitted = file.split("\n\n")
    val instructions = splitted[0].toList()
    val map = parse(splitted[1])
    return Input(instructions, map)
}

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
            val result = someFun()
            println("$toPrint $result")
        }
    println("$toPrint $funTime")

}

data class Input(val instructions: List<Char>, val map: Map<String, Target>)

data class Target(val left: String, val right: String)

fun solveGoodOne(input: Input): Long {
    val startValues = input.map.entries.filter { it.key.last() == 'A' }.map { it.key }
    return goodOne(startValues, input.map, input.instructions.toList())
}

fun solveBruteForce(input: Input): Long {
    val startValues = input.map.entries.filter { it.key.last() == 'A' }.map { it.key }
    val time = measureTime {
        bruteForce(startValues, input.instructions, input.map)
    }
    println("brute real time $time")
    return bruteForce(startValues, input.instructions, input.map)
}

tailrec fun bruteForce(values: List<String>, instructions: List<Char>, map: Map<String, Target>, index: Int = 0, count: Long = 0): Long {

    val instruction = instructions[index]
    val currentValues = values.map {
        getNextValue(it, map, instruction)
    }
    val currentCount = count + 1
    return if (currentValues.all { it.last() == 'Z' }) {
        currentCount
    } else {
        bruteForce(
            currentValues,
            instructions,
            map,
            index = if (index == instructions.size - 1) 0 else index + 1,
            currentCount
        )
    }
}

fun solveLcm(input: Input): Long {
    val startValues = input.map.entries.filter { it.key.last() == 'A' }.map { it.key }
    val result = doSolveLcm(startValues, input.map, input.instructions.toList())
    return result
}

fun doSolveLcm(startValues: List<String>, map: Map<String, Target>, instructions: List<Char>): Long {
    val valuesAcc: List<Triple<String, Long, Int>> = startValues.map { Triple(it, 0, 0) }
    val newValuesAcc = valuesAcc.map { (startValue, counterAcc, indexAcc) ->
        var found = false
        var counter = 0
        var index = indexAcc
        var value = startValue
        while (!found) {
            counter += 1
            value = getNextValue(value, map, instructions[index])
            if (index == instructions.size - 1) {
                index = 0
            } else index += 1
            if (value.last() == 'Z') {
                found = true
            }
        }
        Triple(value, counter + counterAcc, index)
    }
    return newValuesAcc.fold(1.toLong()) { acc, triple ->
        lcm(acc, triple.second)
    }
}

fun goodOne(startValues: List<String>, map: Map<String, Target>, instructions: List<Char>): Long {
    val countersUntilCycling = startValues.map { value ->
        getCountersUntilCycling(
            value = value,
            map = map,
            allInstructions = instructions,
        ).sorted()
    }
    val match = countersUntilCycling
        .first()
        .find { value -> countersUntilCycling.all { value in it } }
    return match ?: countersUntilCycling.map { it.last() }.fold(1.toLong()) { acc, value -> lcm(acc, value) }
}


data class Trip(val from: String, val to: String, val instructions: List<Char>)

tailrec fun getCountersUntilCycling(
    value: String,
    map: Map<String, Target>,
    allInstructions: List<Char>,
    trips: Set<Trip> = setOf(),
    index: Int = 0,
    counters: List<Long> = listOf(),
    count: Long = 0,
    currentInstructions: List<Char> = listOf()
): List<Long> {
    val instruction = allInstructions[index]
    val next = getNextValue(value, map, instruction)
    val nextIsZ = next.last() == 'Z'
    val currentTrip = Trip(
        from = value,
        to = next,
        instructions = currentInstructions + instruction
    )
    val newCurrentCount = count + 1
    return if (currentTrip in trips) {
        // Stop the recursive loop. Now lcm applies.
        counters
    } else {
        getCountersUntilCycling(
            value = next,
            map = map,
            allInstructions = allInstructions,
            trips = if (nextIsZ) trips + currentTrip else trips,
            index = if (index == allInstructions.size - 1) 0 else index + 1,
            counters = if (nextIsZ) counters + newCurrentCount else counters,
            count = newCurrentCount,
            currentInstructions = if (nextIsZ) listOf() else currentInstructions + instruction
        )
    }
}

fun gcd(a: Long, b: Long): Long {
    if (b == 0.toLong()) return a
    return gcd(b, a % b)
}

fun lcm(a: Long, b: Long): Long {
    return a / gcd(a, b) * b
}

fun getNextValue(value: String, map: Map<String, Target>, instruction: Char) = when (instruction) {
    'R' -> map[value]?.right ?: throw Exception("$value does not exist in map $map")
    'L' -> map[value]?.left!!
    else -> throw Exception("Cannot happen $instruction")
}

fun parse(input: String): Map<String, Target> {
    val lines = input.split("\n").dropLast(1)
    return lines.associate {
        doParse(it)
    }
}

fun doParse(line: String): Pair<String, Target> {
    val first = line.substringBefore(" =")
    val second = line.substringAfter("= (").substringBefore(",")
    val third = line.substringAfter(", ").substringBefore(")")
    return first to Target(left = second, right = third)
}
