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
	return solve(input.lines, 25)
}

func solve(line []int, count int) int {
	frequencyMap := map[int]int{}
	for _, value := range line {
		frequencyMap[value] += 1
	}

	for i := 1; i <= count; i++ {
		frequencyMapCopy := map[int]int{}
		for value, frequency := range frequencyMap {
			frequencyMapCopy[value] = frequency
		}

		for value, frequency := range frequencyMapCopy {
			strValue := strconv.Itoa(value)
			frequencyMap[value] -= frequency

			if value == 0 {
				frequencyMap[1] += frequency
			} else if len(strValue)%2 == 0 {

				splitAt := len(strValue) / 2
				first, _ := strconv.Atoi(strValue[0:splitAt])
				second, _ := strconv.Atoi(strValue[splitAt:])

				frequencyMap[first] += frequency
				frequencyMap[second] += frequency
			} else {
				newValue := value * 2024
				frequencyMap[newValue] += frequency
			}
		}

	}
	return lo.Sum(lo.Values(frequencyMap))
}

func blink(lines []int) []int {
	resut := lo.Reduce(lines, func(acc []int, value int, _ int) []int {
		return append(acc, next(value)...)
	}, []int{})
	return resut
}

func next(value int) []int {
	if value == 0 {
		return []int{1}
	}
	strValue := strconv.Itoa(value)
	if len(strValue)%2 == 0 {
		splitAt := len(strValue) / 2
		first, _ := strconv.Atoi(strValue[0:splitAt])
		second, _ := strconv.Atoi(strValue[splitAt:])

		return []int{first, second}
	}
	return []int{value * 2024}
}

func part2(input Input) int {
	return solve(input.lines, 75)
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
