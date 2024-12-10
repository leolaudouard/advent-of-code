package main

import (
	//"github.com/samber/lo"
	"strings"
	_ "embed"
	"fmt"
)

//go:embed inputTest.txt
var inputTest string

//go:embed input.txt
var input string

type Input struct {
    lines []string
    maxX int
    maxY int
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
    return 0
}

func part2(input Input) int {
    return 0
}

func main() {
    run(input, 1, "inputTest")
    run(input, 1, "inputTest")
    run(input, 2, "input")
    run(input, 2, "input")
}

func run(input string, part int, inputType string) {
    fmt.Printf("Result part %v %v", part, inputType)
    var result int 
    if part == 1 {
        result = part2(parse(input))
    } else {
        result = part2(parse(input))
    }
    fmt.Println()
    fmt.Println(result)
}
