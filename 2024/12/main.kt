import java.io.File
import kotlin.math.abs
import kotlin.time.measureTime

fun main() {
    val inputTest = getInput("inputTest.txt")
    val input = getInput("input.txt")
    println("-------- PART 1 --------")
    println(inputTest)
    println(input.lines.first())
    println(input.lines.last())
    //mainMeasuringTime({ part1(inputTest) }, "InputTest")
    //mainMeasuringTime({ part1(input) }, "Input")
    println("\n-------- PART 2 --------")
    mainMeasuringTime({ part2(inputTest) }, "InputTest")
    //mainMeasuringTime({ part2(input) }, "Input")
}

fun mainMeasuringTime(someFun: () -> Long, toPrint: String) {
    val funTime = measureTime {
        val result = someFun()
        println("$toPrint $result")
    }
    println("$toPrint exec time $funTime")

}

private fun getInput(fileName: String): Input {
    val file = File(fileName).readText()
    val lines = file.split("\n").dropLast(1)

    val maxY = lines.size - 1
    val maxX = lines[0].length - 1
    return Input(lines, maxX, maxY)
}

data class Input(val lines: List<String>, val maxX: Int, val maxY: Int)
data class Pose(val x: Int, val y: Int)
data class Group(val char: Char, val poses: List<Pose>)

private fun part2(input: Input): Long {
    val groups = input.lines.withIndex().fold(listOf<Pair<Char, List<Group>>>()) { acc, (y, value) ->
        value.withIndex().fold(acc) { charToGroupsMap, (x, char) ->
            val pose = Pose(x, y)
            val currentGroups = charToGroupsMap.filter { it.first == char }.map { it.second }.flatten()

            if (pose in currentGroups.flatMap { it.poses }) {
                charToGroupsMap
            } else {

                val group = exploreGroup(char, listOf(pose), input, Group(char, listOf(pose)), setOf())
                val groups = currentGroups.plus(group)
                println("New group done ${group.char}, ${group.poses.size} Groups count ${groups.size}")
                val pair = char to listOf(group)
                charToGroupsMap.plus(pair)
            }
        }
    }.map { it.second }.flatten().distinct()
    println("Groups done")
    println(groups.joinToString("\n"))
    return groups.sumOf { group: Group ->
        val borders = group.poses.map { pose ->
            val next = nextPoses(pose)
            next.filter { p -> input.lines.getOrNull(p.y)?.getOrNull(p.x) != group.char }
        }.distinct().flatten()
        val vertical = borders.sortedBy { it.x }.sortedBy { it.y }.groupBy { it.x }.filter { it.value.size > 1 }
        val okokV = vertical.values.fold(0) { acc, poses: List<Pose> ->
            acc + poses.withIndex().fold(0) { poseAcc, (i, p) ->
                val prevOrNull = poses.getOrNull(i - 1)
                if (prevOrNull == null || abs(prevOrNull.x - p.x) > 1) {
                    acc + 1
                } else acc

            }
        }


        val horizontal = borders.sortedBy { it.y }.sortedBy { it.x }.groupBy { it.y }
        val okokH = horizontal.values.fold(0) { acc, poses: List<Pose> ->
            acc + poses.withIndex().fold(0) { poseAcc, (i, p) ->
                val prevOrNull = poses.getOrNull(i - 1)
                if (prevOrNull == null || abs(prevOrNull.x - p.x) > 1) {
                    val char = group.char
                    println("found vertice at $p for $char")
                    acc + 1
                } else acc

            }
        }

        println("Vertices V:$okokV, H:$okokH")
        //println(vertices.joinToString("\n"))
        val perimeter = okokV + okokH
        val area = group.poses.size
        val result = perimeter * area
        println("A region of ${group.char} plants with price $area * $perimeter = $result.")
        result
    }.toLong()
}

private fun part1(input: Input): Long {
    val groups = input.lines.withIndex().fold(listOf<Pair<Char, List<Group>>>()) { acc, (y, value) ->
        value.withIndex().fold(acc) { charToGroupsMap, (x, char) ->
            val pose = Pose(x, y)
            val currentGroups = charToGroupsMap.filter { it.first == char }.map { it.second }.flatten()

            if (pose in currentGroups.flatMap { it.poses }) {
                charToGroupsMap
            } else {

                val group = exploreGroup(char, listOf(pose), input, Group(char, listOf(pose)), setOf())
                val groups = currentGroups.plus(group)
                println("New group done ${group.char}, ${group.poses.size} Groups count ${groups.size}")
                val pair = char to listOf(group)
                charToGroupsMap.plus(pair)
            }
        }
    }.map { it.second }.flatten().distinct()
    println("Groups done")
    println(groups.joinToString("\n"))
    return groups.sumOf { group: Group ->
        val borders = group.poses.flatMap { pose ->
            val next = nextPoses(pose)
            next.filter { p -> input.lines.getOrNull(p.y)?.getOrNull(p.x) != group.char }
        }
        println("borders")
        println(borders)
        val perimeter = borders.size //.filter { it == null }.size +  groupsOrNull.filterNotNull().sumOf { it.area() }
        val area = group.poses.size
        val result = perimeter * area
        println("A region of ${group.char} plants with price $area * $perimeter = $result.")
        result
    }.toLong()
}

tailrec fun exploreGroup(char: Char, poses: List<Pose>, input: Input, group: Group, seen: Set<Pose>): Group {
    println("Poses len ${poses.size}")
    val next = nextPosesInGroup(char, poses, input).filter { p -> p !in group.poses }
    val newGroup = group.copy(poses = group.poses + next)
    if (next.isEmpty()) {
        return newGroup
    }
    val filtered = next.filter {
        it !in seen
    }
    return exploreGroup(char, filtered, input, newGroup, seen + poses)
}

fun nextPosesInGroup(char: Char, poses: List<Pose>, input: Input): List<Pose> {
    return listOf((1 to 0), (0 to 1), (-1 to 0), (0 to -1)).map { (dx, dy) ->
        poses.map { pose ->
            Pose(pose.x + dx, pose.y + dy)
        }
    }.flatten().filter { p ->
        (p.x <= input.maxX && p.y <= input.maxY && p.x >= 0 && p.y >= 0)
                &&
                char == input.lines[p.y][p.x]
    }.distinct()
}

fun nextPoses(pose: Pose): List<Pose> {
    return listOf((1 to 0), (0 to 1), (-1 to 0), (0 to -1)).map { (dx, dy) ->
        Pose(pose.x + dx, pose.y + dy)
    }
}
