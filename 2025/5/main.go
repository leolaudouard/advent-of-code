package main

import (
	//"github.com/samber/lo"
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
	numbers []int64
}

type Range struct {
	before int64
	after int64
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
	var numbers []int64
	for _, numberStr := range numbersStr {
		number, _ := strconv.ParseInt(numberStr, 10, 64)
		numbers = append(numbers, number)
	}

	var ranges []Range
	for _, rg := range lines[:index] {
		ok := strings.Split(rg, "-")
		before, _ := strconv.ParseInt(ok[0], 10, 64)
		after, _ := strconv.ParseInt(ok[1], 10, 64)
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

func isFresh(number int64, ranges []Range) bool {
	for _, rg := range ranges {
		if number >= rg.before && number <= rg.after {
			return true
		}
	}

	return false
}

func part2(input Input) int {
	return 2
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
