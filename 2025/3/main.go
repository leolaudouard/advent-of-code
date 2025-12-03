package main

import (
	//"github.com/samber/lo"
	_ "embed"
	"fmt"
	"os"
	"strconv"
	"strings"
	"slices"
	"time"
)

type Input struct {
	lines []string
	maxX  int
	maxY  int
}

func parse(value string) Input {
	lines := strings.Split(value, "\n")
	maxY := len(lines) - 2
	maxX := len(lines[0]) - 1
	return Input{
		lines,
		maxX,
		maxY,
	}
}

func part1(input Input) int {
	total := 0
	for _, bank := range input.lines {
		if len(bank) > 0 {
			total += joltage(strings.Split(bank, ""))
		}
	}
	return total
}

func joltage(bank []string) int {
	i, maxValue := maxWithIndex(bank[:len(bank)-1])
	nextMaxValue := slices.Max(bank[i+1:])
	fmt.Println("Bank ", bank)
	fmt.Println("Joltage for bank is ", maxValue, nextMaxValue)
	joltage, _ := strconv.Atoi(maxValue + nextMaxValue)
	return joltage
}

func maxWithIndex(bank []string) (int, string)  {
	maxValue := slices.Max(bank)
	for i, value := range bank {
		if maxValue == value {
			return i, maxValue
		}
	}
	return 0, "0"
}

func part2(input Input) int {
	total := 0
	for _, bank := range input.lines {
		if len(bank) > 0 {
			joltage := joltagePart2(strings.Split(bank, ""), "", 12)
			fmt.Println("Joltage for bank", bank)
			fmt.Println("Is", joltage)
			total += joltage
		}
	}
	return total
}

func joltagePart2(bank []string, joltageAcc string, digits int) int {
	fmt.Println("joltagePart2", joltageAcc)
	if digits == 0 {
		fmt.Println("Stop with digits", digits)
	  joltage, _ := strconv.Atoi(joltageAcc)
		return joltage
	}
	i, maxValue := maxWithIndex(bank[:len(bank)-digits])
	nextMaxValue := slices.Max(bank[i+1:])
	newAcc := joltageAcc + maxValue + nextMaxValue
	fmt.Println("newAcc", maxValue, nextMaxValue, newAcc)
	return joltagePart2(bank[i+1:], newAcc, digits - 1)
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
