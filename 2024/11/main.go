package main

import (
	_ "embed"
	"fmt"
	"strconv"
	"strings"

	"github.com/samber/lo"
)

//go:embed inputTest.txt
var inputTest string

//go:embed input.txt
var input string

type Input struct {
	lines []int
}

func parse(value string) Input {
	lines := strings.Split(strings.Split(value, "\n")[0], " ")

	linesInt := lo.Map(lines, func(value string, _ int) int {
		newValue, _ := strconv.Atoi(value)
		return newValue
	})
	return Input{
		linesInt,
	}
}

func part1(input Input) int {
	return solve(input, 25)
}

func solve(input Input, count int) int {
	lines := input.lines
	fmt.Println("Initial arrangement:")
	fmt.Printf("%v", lines)
	for y := 1; y <= count; y++ {
		fmt.Printf("After %d blink:", y)
		fmt.Println()
		lines = blink(lines)
	}
	return len(lines)
}

func blink(lines []int) []int {
	resut := lo.Reduce(lines, func(acc []int, value int, _ int) []int {
		if value == 0 {
			return append(acc, 1)
		}
		strValue := strconv.Itoa(value)
		if len(strValue)%2 == 0 {
			splitAt := len(strValue) / 2
			first, _ := strconv.Atoi(strValue[0:splitAt])
			second, _ := strconv.Atoi(strValue[splitAt:])

			return append(acc, []int{first, second}...)
		}
		return append(acc, value*2024)
	}, []int{})
	return resut
}

func part2(input Input) int {
	return solve(input, 75)
}

func main() {
	run(input, 1, "inputTest")
	run(input, 1, "input")
	run(input, 2, "inputTest")
	run(input, 2, "input")
}

func run(input string, part int, inputType string) {
	fmt.Printf("Result part %v %v", part, inputType)
	var result int
	if part == 1 {
		if inputType == "inputTest" {
			result = part1(parse(inputTest))
		} else {
			result = part1(parse(input))
		}
	} else {
		if inputType == "inputTest" {
			result = part2(parse(inputTest))
		} else {
			result = part2(parse(input))
		}
	}
	fmt.Println()
	fmt.Println(result)
	fmt.Println()
}
