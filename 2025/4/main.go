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
	fmt.Println()

	next, sum := removeRolls(input.lines)
	printInput(next)
	return sum
}

func removeRolls(lines []string) ([]string, int) {
	sum := 0
	var nextLines []string
	for y, line := range lines {
		runes := []rune(line)
		for x, char := range strings.Split(line, "") {
			if (char == "@") {
				adjacentRollsCount := adjacentRollsCount(lines, x, y)
				if (adjacentRollsCount < 4) {
					runes[x] = 'x'
					sum++
				}
			}
		}
		nextLines = append(nextLines, string(runes))
	}

	return nextLines, sum
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
	printInput(input.lines)
	shouldContinue := true
	next := input.lines
	rollsRemovedTotal := 0
	for shouldContinue {
		sum := 0
		next, sum = removeRolls(next)
		rollsRemovedTotal += sum
		
		fmt.Println("Rolls removed ", sum)
		fmt.Println()
		if sum == 0 {
			shouldContinue = false
		}


		printInput(input.lines)

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


func printInput(lines []string) {
		fmt.Println()
		for _, line := range lines {
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
