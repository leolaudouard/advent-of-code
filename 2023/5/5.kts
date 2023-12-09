import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.math.abs
import kotlin.system.exitProcess

val inputTest = File("inputTest.txt").readText()
val input = File("input.txt").readText()
val linesTest = inputTest.split('\n')
val lines = input.split('\n')

val seedToSoilStr = "seed-to-soil map:"
val soilToFertilizerStr = "soil-to-fertilizer map:"
val fertilizerToWaterStr = "fertilizer-to-water map:"
val waterToLightStr = "water-to-light map:"
val lightToTemperatureStr = "light-to-temperature map:"
val temperatureToHumidityStr = "temperature-to-humidity map:"
val humidityToLocationStr = "humidity-to-location map:"

fun main() {
    //val testResult = solve(linesTest)
    //println("testResult $testResult")
    val result = solve(lines)
    println("result $result")
}
main()


data class File(
    val seeds: List<Pair<Long, Long>>,
    val seedToSoil: List<ALine>,
    val soilToFertilizer: List<ALine>,
    val fertilizerToWater: List<ALine>,
    val waterToLight: List<ALine>,
    val lightToTemperature: List<ALine>,
    val temperatureToHumidity: List<ALine>,
    val humidityToLocation: List<ALine>,
)

data class ALine(val source: Long, val destination: Long, val range: Int)

fun solve(lines: List<String>) {
    val file: File = parse(lines)
    file.seeds.forEach { seed ->
        println("Seed ${seed.first}, range: ${seed.second}")
        val min = (0..seed.second - 1).map { toAdd ->
            location((seed.first + toAdd), file)
        }.min()
        println("Min $min")
    }
}

fun location(seed: Long, file: File): Long {
    val soil = brah(file.seedToSoil, seed)

    val fertilizer = brah(file.soilToFertilizer, soil)
    val water = brah(file.fertilizerToWater, fertilizer)
    val light = brah(file.waterToLight, water)
    val temperature = brah(file.lightToTemperature, light)
    val humidity = brah(file.temperatureToHumidity, temperature)
    val location = brah(file.humidityToLocation, humidity)
    return location
}

fun brah(lines: List<ALine>, value: Long): Long {
    return lines.find { line ->
        val diff = (value - line.source)
        if (diff >= 0.toLong() && diff < line.range.toLong()) {
            true
        } else false
    }?.let {
        val diff = (value - it.source)
        it.destination + diff
    } ?: value
}

fun parse(lines: List<String>): File {
    val parsedSeeds = lines.first().substringAfter("seeds:").split(" ").mapNotNull { it.toLongOrNull() }
    val seeds = parsedSeeds.filterIndexed { index, _ -> index % 2 == 0 }
    val ranges = (parsedSeeds.toSet() - seeds.toSet()).toList()
    val seedsToRanges = seeds.zip(ranges)
    val seedToSoil = doParse(lines, seedToSoilStr, soilToFertilizerStr)
    val soilToFertilizer = doParse(lines, soilToFertilizerStr, fertilizerToWaterStr)
    val fertilizerToWater = doParse(lines, fertilizerToWaterStr, waterToLightStr)
    val waterToLight = doParse(lines, waterToLightStr, lightToTemperatureStr)
    val lightToTemperature = doParse(lines, lightToTemperatureStr, temperatureToHumidityStr)
    val temperatureToHumidity = doParse(lines, temperatureToHumidityStr, humidityToLocationStr)
    val humidityToLocation = doParse(lines, humidityToLocationStr, null)
    return File(
        seeds = seedsToRanges,
        seedToSoil,
        soilToFertilizer = soilToFertilizer,
        fertilizerToWater = fertilizerToWater,
        waterToLight = waterToLight,
        lightToTemperature = lightToTemperature,
        temperatureToHumidity = temperatureToHumidity,
        humidityToLocation = humidityToLocation
    )
}

fun doParse(lines: List<String>, delimiter: String, secondsDelimiter: String?): List<ALine> {
    val filteredLines = lines
        .dropWhile { delimiter !in it }
        .drop(1)
        .takeWhile { line ->
            secondsDelimiter?.let {
                secondsDelimiter !in line
            } ?: true
        }
        .filter { line -> line.any { it.digitToIntOrNull() == null } }

    val lines = filteredLines.map { line ->
        parseLine(line)
    }
    /*
        val map = lines.fold(mapOf<BigInteger, BigInteger>()) { acc, line ->
            acc + (0..<line.range).associate { toAdd -> line.source + toAdd.toBigInteger() to line.destination + toAdd.toBigInteger() }
        }
    */
    return lines
}


fun parseLine(line: String): ALine = line.split(" ").map { it.toLong() }.let {
    ALine(
        source = it[1],
        destination = it[0],
        range = it[2].toInt()
    )
}