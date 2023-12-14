import java.io.File
import kotlin.system.exitProcess
import kotlin.time.measureTime

fun main() {
    //val inputTest = Input(listOf(Line("???.###", listOf(1, 1, 3))))

    //val inputTest = Input(listOf(Line("#??.######..#####.", listOf(1, 6, 5))))
    //val inputTest = Input(listOf(Line("#??.##", listOf(1, 2))))

    //unitTest()
    val inputTest = getInput("inputTest.txt")
    mainMeasuringTime({ solve(inputTest) }, "InputTest")
    val input = getInput("input.txt")
    mainMeasuringTime({ solve(input) }, "Input")
}
main()

data class Input(val lines: List<Line>)
data class Line(val str: String, val contiguousGroups: List<Int>)

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint $funTime")

}

fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val splitted = file.split("\n").map { line ->
        val group = line.split(" ")[0]
        val contiguousGroup = line.split(" ")[1].split(",").map { char -> char.toInt() }

        Line(
            group + "?" + group + "?" + group + "?" + group + "?" + group,
            contiguousGroup + contiguousGroup + contiguousGroup + contiguousGroup + contiguousGroup
        )
    }
    return Input(splitted)
}

fun solve(input: Input): Long {
    return input.lines.map { line ->

        val count =
            getCombinationCount(line)
        println(count)
        count
    }.sum()
}

fun getCombinationCount(line: Line): Long {
    return recursiveFun(line.str, line.contiguousGroups, 0)
}

fun unitTest() {
    var ok = "????????????????????"
    (0..10).map {
        ok += "?"
        println("Len ${ok.length}")
        println(recursiveFun(ok, listOf(7), 0))

    }
    exitProcess(1)
}

tailrec fun recursiveFun(strRest: String, remainingGroups: List<Int>, currentCount: Int = 0): Long {
    val maybe = speedThatUp(strRest, currentCount, remainingGroups)
    if (maybe != null) {
        return maybe.sumOf {
            val (newRest, newGroups, newCount) = it
            newCount.second * recursiveFun(newRest, newGroups, newCount.first)
        }
    }

    //println("Remaining groups $remainingGroups")
    //println("strRest $strRest, currentCount: $currentCount")
    val currentCharOrNull: Char? = strRest.firstOrNull()
    val currentGroup = remainingGroups.firstOrNull()
        ?: if (strRest.contains("#")) {
            //println("Returning 0 because no group but left some #")
            return 0
        } else {
            //println("Returning 1 because no more groups")
            return 1
        }

    val disjoint = if (currentCharOrNull == null) true else (currentCharOrNull == '.')
    if (currentCount == currentGroup && disjoint) {
        //println("Drop group $currentGroup, $strRest, $remainingGroups")
        return recursiveFun(
            strRest,
            remainingGroups.drop(1),
            0
        )
    }
    val currentChar = if (currentCharOrNull == null) {
        //println("Returning 0 bc null currentChar")
        return 0
    } else currentCharOrNull
    if (currentCount == currentGroup && currentChar == '#') {
        //println("Returning 0 bc currentCount match but not right currentChar")
        return 0
    }
    if (shouldStop(strRest, currentCount, remainingGroups)) {
        //println("Returning 0 bc strLenght & currentCount to low.")
        return 0
    }
    val rest = strRest.drop(1)
    val restForPoint = ".$rest"
    val restForH = "#$rest"
    return when (currentChar) {
        '.' -> if (currentCount == 0) recursiveFun(
            strRest = rest,
            remainingGroups = remainingGroups,
            currentCount = 0
        ) else 0

        '#' -> recursiveFun(
            strRest = rest,
            remainingGroups = remainingGroups,
            currentCount = currentCount + 1
        )

        '?' -> {
            //println("Split on H")
            //println("Remaining groups $remainingGroups")
            //println("restForH $restForH, currentCount: $currentCount")
            val h = recursiveFun(restForH, remainingGroups, currentCount)

            //println("Split on P")
            //println("Remaining groups $remainingGroups")
            //println("restForP $restForPoint, currentCount: $currentCount")
            val p = recursiveFun(
                restForPoint,
                remainingGroups,
                currentCount,
            )
            h + p
        }

        else -> throw Exception("Boom $currentChar")
    }
}

fun shouldStop(
    strRest: String,
    currentCount: Int,
    remainingGroups: List<Int>
) = notEnoughChars(
    strRest,
    currentCount,
    remainingGroups
)

fun speedThatUp(
    strRest: String,
    currentCount: Int,
    remainingGroups: List<Int>
): List<Triple<String, List<Int>, Pair<Int, Int>>>? {
    val restUntilPoint = strRest.takeWhile { it != '.' }
    val currentMaxCount = restUntilPoint.length + currentCount
    val currentGroup = remainingGroups.firstOrNull() ?: return null
    val newRest = strRest.dropWhile { it != '.' }
    if (newRest == strRest) return null
    return if (currentMaxCount < currentGroup) {
        // This group ain't going to make it. Skip it.
        listOf(Triple(newRest, remainingGroups, currentCount to 1))
    } else if (currentMaxCount == currentGroup) {
        // This group made it; Don't bother exploring
        listOf(Triple(newRest, remainingGroups.drop(1), 0 to 1))
        null
    } else if (currentMaxCount > currentGroup) {
        val diff = currentMaxCount - currentCount
        if (diff == 1) {
            val replacableCount = restUntilPoint.filter { it == '?' }
            (0..replacableCount.length - 1).map {
                Triple(newRest, remainingGroups.drop(1), 0 to 1)
            }
        } else {
            if (restUntilPoint.all { it == '?' }) {
                //currentGroup 2  to 5 replacableCount gives 1
                // N possibilities to make this group.
                // What to add to
                val possibleValueCount = restUntilPoint.length - (currentGroup - 1)
                listOf(
                    Triple(newRest, remainingGroups.drop(1), 0 to possibleValueCount),
                    Triple(newRest, remainingGroups.drop(1), 0 to 1),
                )
                // TODO: this clause is so wrong
                null
            } else null
        }
    } else null
}


fun notEnoughChars(
    strRest: String,
    currentCount: Int,
    remainingGroups: List<Int>
) = strRest.filter { it != '.' }.length + currentCount < remainingGroups.sum()
