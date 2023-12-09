import java.io.File
import kotlin.math.abs

val inputTest = File("inputTest.txt").readText()
val input = File("input.txt").readText()
val linesTest = inputTest.split('\n')
val lines = input.split('\n')


fun main() {
    val testResult = solve(linesTest)
    val result = solve(lines)
    println("TestResult $testResult")
    println("result $result")
}
main()


data class TypeMe(val str: String)

fun solve(lines: List<String>): Long {
    val time = lines[0].substringAfter("Time:").filter { it.digitToIntOrNull() != null }.let { it.toLong() }
    val distance = lines[1].substringAfter("Distance:").filter { it.digitToIntOrNull() != null }.let { it.toLong() }
    val min = findMin(time, distance)
    val max = findMax(time, distance)

    return (max - min) + 1
}

fun findMin(time: Long, distance: Long): Long {
    return doFind((0..time).toList(), time, distance)
}

fun findMax(time: Long, distance: Long): Long {
    return doFind((0..time).reversed().toList(), time, distance)
}

fun doFind(list: List<Long>, time: Long, distance: Long): Long {
    return list.find { timePressingButton ->
        ((time - timePressingButton) * timePressingButton) > distance
    }!!
}

fun parse(line: String): Int {

    return 2
}

fun doSolve(lines: List<String>): String = "yep"