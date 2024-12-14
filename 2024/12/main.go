package main

import (
	//"github.com/samber/lo"
	_ "embed"
	"fmt"
	"math"
	"os"
	"reflect"
	"strconv"
	"strings"
	"time"

	//"github.com/samber/lo"
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

type search struct {
	area      int
	perimeter int
}
type move struct {
	dx int
	dy int
}

type Pose struct {
	x int
	y int
}

type Group struct {
	poses []Pose
}


func part1(input Input) int {
	posesMap := map[string][]Pose{}
	for y, line := range input.lines {
		for x, char := range line {
			oldPoses := posesMap[string(char)]
			posesMap[string(char)] = append(oldPoses, Pose{x, y})
		}
	}

	//groups := map[string][]Group
	currentPoses := []Pose{{0, 0}}
	condition := true
	for condition {
		fmt.Printf("Current poses %v", currentPoses)
		fmt.Println()
		nextPoses := nextPoses(currentPoses, input)

		if reflect.DeepEqual(nextPoses, currentPoses) {
			condition = false
			return 42
		}
		currentPoses = nextPoses
	} 
	return 1
}

func nextPoses(poses []Pose, input Input) []Pose{
	newPoses := []Pose{}
	for _, pose := range poses {
		nextRight := Pose{pose.x + 1, pose.y}
		if (nextRight.x <= input.maxX && nextRight.y <= input.maxY) {
			newPoses = append(newPoses, nextRight)
		}

		nextBottom := Pose{pose.x, pose.y + 1}
		if (nextBottom.x <= input.maxX && nextBottom.y <= input.maxY) {
			newPoses = append(newPoses, nextBottom)
		}
	}
	return newPoses
}

func areAdjacent(pose Pose, otherPose Pose) bool {
	if math.Abs(float64(pose.x-otherPose.x)) <= 1 && math.Abs(float64(otherPose.y-pose.y)) <= 1 {
		return true
	}
	return false
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
