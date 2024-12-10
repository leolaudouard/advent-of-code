package main

import (
	_ "embed"
	"fmt"
	"strings"

	"github.com/samber/lo"
)

//go:embed inputTest.txt
var inputTest string

//go:embed input.txt
var input string

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

type point struct {
	value int
	x     int
	y     int
}

func part1(input Input) int {

	startPoints := []point{}
	for x, line := range input.lines {
		for y, char := range line {
			if string(char) == "0" {
				startPoints = append(startPoints, point{0, x, y})
			}
		}
	}
	result := lo.Map(startPoints, func(startPoint point, _ int) int {
		return len(lo.Uniq(getTrailheads(input, startPoint)))
	})
	fmt.Println(result)
	return lo.Sum(result)
}

type displ struct {
	dx int
	dy int
}

func getTrailheads(input Input, startPoint point) []point {
	nexts := lo.FilterMap([]displ{{1, 0}, {-1, 0}, {0, 1}, {0, -1}}, func(move displ, _ int) (point, bool) {
		newX, newY := startPoint.x+move.dx, startPoint.y+move.dy
		if newX > input.maxX || newY > input.maxY || newX < 0 || newY < 0 {
			return point{0, 0, 0}, false
		}
		value := input.lines[newX][newY]
		if string(value) != "." {
			intValue := int(value - '0')
			if intValue-startPoint.value == 1 {
				return point{intValue, newX, newY}, true
			}
		}
		return point{0, 0, 0}, false
	})

	targetFounds := lo.Filter(nexts, func(p point, _ int) bool {
		return p.value == 9
	})
	rest := lo.Filter(nexts, func(p point, _ int) bool {
		return p.value != 9
	})

	nextTargets := lo.FlatMap(rest, func(p point, _ int) []point {
		return getTrailheads(input, p)
	})
	total := append(targetFounds, nextTargets...)
	return total
}

func part2(input Input) int {

	startPoints := []point{}
	for x, line := range input.lines {
		for y, char := range line {
			if string(char) == "0" {
				startPoints = append(startPoints, point{0, x, y})
			}
		}
	}
	result := lo.Map(startPoints, func(startPoint point, _ int) int {
		return len(getTrailheads(input, startPoint))
	})
	fmt.Println(result)
	return lo.Sum(result)
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
