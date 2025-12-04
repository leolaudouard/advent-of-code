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
	sum := 0
	fmt.Println()
	for y, line := range input.lines {
		for x, char := range strings.Split(line, "") {
			if (char == "@") {
				adjacentRollsCount := adjacentRollsCount(input.lines, x, y)
				if (adjacentRollsCount < 4) {
					fmt.Print("x")
					sum++
				} else {
					fmt.Print(char)
				}
			} else {
				fmt.Print(char)
			}
		}
		fmt.Println()
	}
	return sum
}

func adjacentRollsCount(lines []string, x int, y int) int {
	count := 0
	for dx := -1; dx <= 1; dx++ {
		for dy := -1; dy <= 1; dy++ {
			newX := x + dx
			newY := y + dy
			if newX >= 0 && newX <= len(lines) {
				if (newY >= 0 && newY < len(lines[newX])) {
					char := lines[newY][newX]
					sameX := newX == x
					sameY := newY == y
					sameBoth := sameY && sameX
					if char == '@' && !sameBoth {
						count++
					}
				}
			}
		}
	}
	return count
}

func part2(input Input) int {
	rollsRemovedTotal := 0
	printInput(input)
	shouldContinue := true
	for shouldContinue {
		rollsRemoved := 0
		for y, line := range input.lines {
			runes := []rune(line)
			for x, char := range string(line) {
				if (char == '@') {
					adjacentRollsCount := adjacentRollsCount(input.lines, x, y)
					if (adjacentRollsCount < 4) {
						runes[x] = 'x'
						rollsRemoved++
					}
				}
			}
			input.lines[y] = string(runes)
		}
		fmt.Println("Rolls removed ", rollsRemoved)
		fmt.Println()
		if rollsRemoved == 0 {
			shouldContinue = false
		}


		printInput(input)

		for y, line := range input.lines {
			runes := []rune(line)
			for x, char := range string(line) {
				if char == 'x' {
						runes[x] = '.'
				}
			}
			input.lines[y] = string(runes)
		}
	}
	return rollsRemovedTotal
}


func printInput(input Input) {
		fmt.Println()
		for _, line := range input.lines {
			for _, char := range string(line) {
					fmt.Print(string(char))
			}
			fmt.Println()
		}
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
