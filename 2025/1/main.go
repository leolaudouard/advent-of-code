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
		lines[:len(lines)-1],
		maxX,
		maxY,
	}
}

func part1(input Input) int {
	value := 50
	dialZeroCount := 0
	fmt.Println("hello")
	for _, line := range input.lines {
		char := string(line[0])
		num, err := strconv.Atoi(line[1:])
		if err != nil {
	     panic("Cannot convert to integer.")
		}

		if char == "L" {
			value -= num
		} else if char == "R" {
			value += num
		}

		 
		for value >= 100 || value < 0 {
			if value >= 100 {
				value -= 100
			} else if value < 0 {
				value += 100
			}
		}
		if value == 0 {
			dialZeroCount += 1
		}
		fmt.Println("Value is")
		fmt.Println(value)
	}
	return dialZeroCount
}

func part2(input Input) int {
	value, count := 50, 0
	fmt.Println("Value is ", value, count)
	for _, line := range input.lines {
		side := string(line[0])
		num, err := strconv.Atoi(line[1:])
		if err != nil {
	     panic("Cannot convert to integer.")
		}


		if side == "L" {
			for i := 1; i <= num;i++ {
				value --
				if value == -1 {
					value = 99
				}

				if value == 0 {
					fmt.Println("DIAL 0")
					count ++
				}
			}
		} else if side == "R" {
			for i := 1; i <= num;i++ {
				value ++
				if value == 100 {
					value = 0
					fmt.Println("DIAL 0")
					count ++
				}
			}
		}

		fmt.Println("Value is ", value, count, side, num)
	}
	return count
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
