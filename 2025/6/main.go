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
	nums []string
	ops []Op
}

type Op struct {
	op string
	i int
}

func parse(value string) Input {
	lines := strings.Split(value, "\n")

	var ops []Op
	for i, op := range lines[len(lines)-2] {
		if op == '*' || op == '+' {
			stuff := Op{
				string(op), 
				i,
			}
			ops = append(ops, stuff)
		}
	}

	numLines := lines[:len(lines)-2]
	var nums []string
	for j, char := range string(numLines[0]) {
		acc := string(char)
		for i:=1;i<len(numLines);i++ {
			acc += string(numLines[i][j])
		}
		nums = append(nums, acc)
	}


	opsI := 0
	op := ops[opsI].op
	var sum uint64
	if op == "*" {
		sum = 1
	} else if op == "+" {
		sum = 0
	}
	var totalSum uint64
	totalSum = 0
	for _, str := range nums {
		if strings.TrimSpace(str) == "" {
			opsI++
			op = ops[opsI].op
			totalSum += sum
			if op == "*" {
				sum = 1
			} else if op == "+" {
				sum = 0
			}
		} else {
			num, _ := strconv.ParseUint(strings.TrimSpace(str), 10, 64)
			if op == "*" {
				sum = sum*num
			} else if op == "+" {
				sum += num
			}
		}

	}
	fmt.Println()
	fmt.Println("Result is ", totalSum + sum)
	return Input{
		nums,
		ops,
	}
}


func part1(input Input) int {
	return 0
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
