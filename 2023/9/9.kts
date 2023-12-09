import java.io.File
import kotlin.time.measureTime

fun main() {
    val input = getInput("input.txt")
    val inputTest = getInput("inputTest.txt")
    mainMeasuringTime({ solve(inputTest) }, "InputTest")
    mainMeasuringTime({ solve(input) }, "Input")
}
main()

data class Input(val lines: List<List<Long>>)
data class TypeMe(val src: String, val dest: String)

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val splitted = file.split("\n").map { line -> line.split(" ").map { it.toLong() } }
    return Input(splitted)
}

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint $funTime")

}

fun solve(input: Input): Long {
    return input.lines.map { list ->
        getExtrapolatedValue(list)
    }.sum()
}

fun getExtrapolatedValue(list: List<Long>): Long {
    val seqs = getSeqs(list, listOf(list)).reversed()
    val extrapolatedValue = seqs.map { longs ->
        longs.first()
    }.fold(0.toLong()) { lastExtrapolatedValue, value ->
        value - lastExtrapolatedValue
    }
    return extrapolatedValue
}

tailrec fun getSeqs(list: List<Long>, seqAcc: List<List<Long>> = listOf()): List<List<Long>> {
    val seq: List<Long> = list.zipWithNext().map { (one, two) -> two - one }
    val newSeqAcc: List<List<Long>> = seqAcc + listOf(seq)
    return if (seq.all { it == 0.toLong() }) newSeqAcc else getSeqs(seq, newSeqAcc)
}

fun getSeq(list: List<Long>) = list.zipWithNext().map { (one, two) -> two - one }
