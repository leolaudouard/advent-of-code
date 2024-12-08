package main

import (
	"strings"
	_ "embed"
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

func part1(value string) int {
    panic("TODO")
}

func part2(value string) int {
    panic("TODO")
}

func main() {
    println(part1(inputTest))
    println(part1(input))
    println(part2(inputTest))
    println(part2(input))
}
