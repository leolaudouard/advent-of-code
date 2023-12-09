import java.io.File
import java.util.*

val inputTest = File("inputTest.txt").readText()
val input = File("input.txt").readText()
val linesTest = inputTest.split('\n').dropLast(1)
val lines = input.split('\n').dropLast(1)


fun main() {
    val testResult = solve(linesTest)
    println("TestResult $testResult")
    val result = solve(lines)
    println("result $result")
}
main()


data class TypeMe(val hand: String, val handType: HandType, val bid: Int)

enum class HandType {
    FiveOfAKind,
    FourOfAKind,
    FullHouse,
    ThreeOfAKind,
    TwoPair,
    OnePair,
    HighCard,
}

fun solve(lines: List<String>): Int {
    val parsedLines = lines.map { line ->
        parse(line)
    }
    val sortedLines = sort(parsedLines)

    println(sortedLines.reversed().joinToString("\n"))
    val result: List<Int> = sortedLines.mapIndexed { index, value ->
        (value.bid * (sortedLines.size - index))
    }

    return result.sum()
}

fun parse(line: String): TypeMe {
    val hand = line.split(" ")[0]
    val bid = line.split(" ")[1].toInt()
    val handType = getHandType(hand)
    return TypeMe(hand, handType, bid)
}

fun sort(lines: List<TypeMe>): List<TypeMe> {
    val map: Map<HandType, List<TypeMe>> = lines.sortedBy { order(it.hand) }.reversed()
        .groupBy { it.handType }


    return listOf(
        map[HandType.FiveOfAKind] ?: listOf(),
        map[HandType.FourOfAKind] ?: listOf(),
        map[HandType.FullHouse] ?: listOf(),
        map[HandType.ThreeOfAKind] ?: listOf(),
        map[HandType.TwoPair] ?: listOf(),
        map[HandType.OnePair] ?: listOf(),
        map[HandType.HighCard] ?: listOf(),
    ).flatten()
}


fun order(card: String): Long = card.map { toNumber(it) }.joinToString("").toLong()

fun toNumber(card: Char): Int = when (card) {
    'A' -> 22
    'K' -> 21
    'Q' -> 20
    'T' -> 19
    '9' -> 18
    '8' -> 17
    '7' -> 16
    '6' -> 15
    '5' -> 14
    '4' -> 13
    '3' -> 12
    '2' -> 11
    'J' -> 10
    else -> 0
}

fun getHandType(hand: String): HandType {
    val frequencies = hand.map { char ->
        char to Collections.frequency(hand.toMutableList(), char)
    }.toMap()
    val mostFrequentCharOrNull= frequencies.filter { it.key != 'J' }.maxByOrNull { it.value }
    val handWithoutJ = if (mostFrequentCharOrNull?.key != null) hand.replace('J', mostFrequentCharOrNull.key) else hand
    val newFrequencies = handWithoutJ.map { char ->
        char to Collections.frequency(handWithoutJ.toMutableList(), char)
    }.toMap()

    return when (newFrequencies.values.sorted()) {
        listOf(5) -> HandType.FiveOfAKind
        listOf(1, 4) -> HandType.FourOfAKind
        listOf(2, 3) -> HandType.FullHouse
        listOf(1, 1, 3) -> HandType.ThreeOfAKind
        listOf(1, 2, 2) -> HandType.TwoPair
        listOf(1, 1, 1, 2) -> HandType.OnePair
        listOf(1, 1, 1, 1, 1) -> HandType.HighCard
        else -> throw Exception("Unreachable $handWithoutJ $newFrequencies")
    }
}