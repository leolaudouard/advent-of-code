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
	line := input.lines[0]
	var invalidIds []int
	for _, r := range strings.Split(line, ","){
		nums := strings.Split(r, "-")
		left, _ := strconv.Atoi(nums[0])
		right, _ := strconv.Atoi(nums[1])
		fmt.Println("Coucou ", left, right)
		for value := left; value <= right; value++ {
				if (isInvalid(value)) {
					invalidIds = append(invalidIds, value)
				}
		}
	}
	sum := 0

	for i := range invalidIds {
		sum += invalidIds[i]
	}
	return sum
}

func isInvalid(value int) bool {
	if value <= 9 {
		return false
	}
	valueStr := strconv.Itoa(value)
	middle := len(valueStr)/2
	firstPart := valueStr[:middle]
	secondPart := valueStr[middle:]
	return firstPart == secondPart || firstPart[0] == '0'
}

func part2(input Input) int {
	line := input.lines[0]
	var invalidIds []int
	for _, r := range strings.Split(line, ","){
		nums := strings.Split(r, "-")
		left, _ := strconv.Atoi(nums[0])
		right, _ := strconv.Atoi(nums[1])
		fmt.Println("Coucou ", left, right)
		for value := left; value <= right; value++ {
				if (isInvalidPart2(value)) {
					invalidIds = append(invalidIds, value)
				}
		}
	}
	sum := 0

	for i := range invalidIds {
		invalidId := invalidIds[i]
		sum += invalidId
	  fmt.Println("Invalid id %v", invalidId)
	}
	return sum
}


func isInvalidPart2(value int) bool {
	if value <= 9 {
		return false
	}
	valueStr := strconv.Itoa(value)
	middle := len(valueStr)/2
	for i := 1; i <= middle; i++ {
		firstPart := valueStr[:i]
		secondPart := valueStr[i:]
		if (i <= len(secondPart)) {
			allValid := true
			for anotherI := i; anotherI <= len(secondPart);anotherI+=i {
				rest := secondPart[anotherI-i:anotherI]
				if value == 1010 {
					fmt.Println("WTF", rest, anotherI, i, secondPart)
				}
				if (rest != firstPart) {
					allValid = false
				}
			}
			return allValid
		}
	}
	return false
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
