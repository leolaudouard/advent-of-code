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
	nums [][]uint64
	ops []string
}

func parse(value string) Input {
	var nums [][]uint64
	lines := strings.Split(value, "\n")
	var ops []string
	for _, op := range lines[len(lines)-2] {
		if op == '*' || op == '+' {
			ops = append(ops, string(op))
		}
	}

	for _, value := range lines[:len(lines)-2] {
	  numsStr := strings.Fields(value)
		for i, numStr := range numsStr {
			  num, _ := strconv.ParseUint(numStr, 10, 64)
				fmt.Println(num)
				if len(nums) > i {
					nums[i] = append(nums[i], num)
				} else {
					var properStuff []uint64
					properStuff = append(properStuff, num)
					nums = append(nums, properStuff)

				}
		}
	}

	return Input{
		nums,
		ops,
	}
}

func part1(input Input) int {
	var totalSum uint64
	totalSum = 0
	for i, nums := range input.nums {
		op := input.ops[i]
		var sum uint64
		if (op == "*") {
			sum = 1
		} else if op == "+" {
			sum = 0
		}
		for _, num := range nums {
		  if (op == "*") {
				sum = sum*num
		  } else if op == "+" {
				sum += num
		  }
		}
		totalSum += sum
	}
	fmt.Println(totalSum)
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
