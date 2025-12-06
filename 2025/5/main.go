package main

import (
	_ "embed"
	"fmt"
	"os"
	"strconv"
	"strings"
	"time"
)

type Input struct {
	lines []string
	maxX  int
	maxY  int
	ranges []Range
	numbers []uint64
}

type Range struct {
	before uint64
	after uint64
}

func parse(value string) Input {
	lines := strings.Split(value, "\n")
	maxY := len(lines) - 2
	maxX := len(lines[0]) - 1
	index := 0
	for i, line := range lines[:len(lines)-1] {
		if line == "" {
			index = i
		}
	}
	numbersStr := lines[index+1:]
	var numbers []uint64
	for _, numberStr := range numbersStr {
		number, _ := strconv.ParseUint(numberStr, 10, 64)
		numbers = append(numbers, number)
	}

	var ranges []Range
	for _, rg := range lines[:index] {
		ok := strings.Split(rg, "-")
		before, _ := strconv.ParseUint(ok[0], 10, 64)
		after, _ := strconv.ParseUint(ok[1], 10, 64)
		ranges = append(ranges, Range{before, after})
	}
	return Input{
		lines[:len(lines)-1],
		maxX,
		maxY,
		ranges,
		numbers,
	}
}

func part1(input Input) int {
	fmt.Println(input.ranges)
	fmt.Println(input.numbers)
	freshSum := 0
	for _, number := range input.numbers {
		if isFresh(number, input.ranges) {
			freshSum ++

		}

	}
	return freshSum
}

func isFresh(number uint64, ranges []Range) bool {
	for _, rg := range ranges {
		if number >= rg.before && number <= rg.after {
			return true
		}
	}

	return false
}

func part2(input Input) int {
	currentRanges := input.ranges
	overlap := true
	loop := 0
	var maxR uint64
	maxR = 0
	var minR uint64
	minR = 100000000000000
	for _, rg := range input.ranges {
		if rg.after > maxR {
			maxR = rg.after
		}

		if rg.before < minR {
			minR = rg.before
		}
	}

	for overlap {
		  loop++
		  fmt.Println(currentRanges)
	    currentRanges = mergeRanges(currentRanges)
			fmt.Println("Looped merge")
			for _, rg := range currentRanges {
			  fmt.Println(rg)
			}
		  overlap = rangesOverlap(currentRanges)
	}
	var sum uint64
	sum = 0
	for _, rg := range currentRanges {
		sum += (rg.after - rg.before + 1)
		fmt.Println(rg)
	}
	var maxR2 uint64
	maxR2 = 0
	var minR2 uint64
	minR2 = 100000000000000
	for _, rg := range currentRanges {
		if rg.after > maxR2 {
			maxR2 = rg.after
		}

		if rg.before < minR2 {
			minR2 = rg.before
		}
	}

	fmt.Println(sum)
	fmt.Println("Max is ", maxR)
	fmt.Println("Max is ", maxR)
	fmt.Println("min is ", minR)
	fmt.Println("min is ", minR2)
	return int(sum)
}


func canBeMerged(rg1 Range, rg2 Range) bool {
	result := (rg1.before >= rg2.before && rg1.before <= rg2.after) || (rg1.after >= rg2.before && rg1.after <= rg2.after)

	return result
}


func rangesOverlap(ranges []Range) bool {
  for i, rg1 := range ranges {
		for j, rg2 := range ranges {
			if i != j && canBeMerged(rg1, rg2)  {
				return true
			}
		}
	}
	return false
}
func mergeRanges(ranges []Range) []Range {
	var currentRanges []Range
	for _, rg := range ranges {
		currentRanges = append(currentRanges, rg)
	}

	var mergedRanges []Range
	var newRanges []Range
  for j, rg := range ranges {
		for i, rg2 := range ranges {
			if i != j && canBeMerged(rg, rg2) {
			  newRange := merge(rg, rg2)
		    fmt.Println("Merging ranges")
				fmt.Println(rg) 
				fmt.Println(rg2)
				fmt.Println(newRange)

				currentRanges = append(currentRanges, newRange)
				mergedRanges = append(mergedRanges, rg)
				mergedRanges = append(mergedRanges, rg2)
				newRanges = append(newRanges, newRange)
			}
		}
	}

	var result []Range
	for _, rg := range currentRanges {
		if (!contains(mergedRanges, rg) && !contains(result, rg)) {
			result = append(result, rg)
		}
	}

	for _, rg := range newRanges {
		if !contains(result, rg) {
			result = append(result, rg)
		}
	}

	return result
}

func contains(ranges []Range, rg1 Range) bool {
	for _, rg2 := range ranges {
		if rg1 == rg2  {
			return true
		}
	}
	return false
}
func merge(rg1 Range, rg2 Range) Range {
	if isIncluded(rg1, rg2) {
		return rg1
	}

	if isIncluded(rg2, rg1) {
		return rg2
	}

	return Range{
		min(rg1.before, rg2.before),
		max(rg1.after, rg2.after),
	}
}

func isIncluded(rg1 Range, rg2 Range) bool {
	return rg1.before <= rg2.before && rg1.after >= rg2.after
}




















//go:embed inputTest.txt
var inputTest string

//go:embed input.txt
var input string

func main() {
	part, err := strconv.Atoi(os.Args[1])
	if err != nil {
		panic("Part 1 or part 2?")
	}

	runInput := inputTest
	inputName := "inputTest"
	if len(os.Args) > 2 && os.Args[2] == "go" {
		inputName = "input"
		runInput = input
	}
	fmt.Printf("Part %v %v", part, inputName)
	run(runInput, part)
}

func timeTrack(start time.Time) {
	elapsed := time.Since(start)
	fmt.Printf("Took %v", elapsed)
}

func run(input string, part int) {
	defer timeTrack(time.Now())

	var result int

	if part == 1 {
		result = part1(parse(input))
	} else {
		result = part2(parse(input))
	}
	fmt.Println()
	fmt.Println(result)
}
