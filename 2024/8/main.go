package main

import (
	"fmt"
	"strings"

	_ "embed"
)

//go:embed inputTest.txt
var inputTest string

//go:embed input.txt
var input string

type Input struct {
    lines []string
    frequencies map[string][][2]int
    maxX int
    maxY int
}

func parse(value string) Input {
    frequencies := map[string][][2]int{}
    lines := strings.Split(value, "\n")
    maxY := len(lines) - 2
    maxX := len(lines[0]) - 1
    for y := range lines {
        for x := range lines[y] {
            value := string(lines[y][x])
            positions := frequencies[value]
            position := [2]int{x, y}
            positions = append(positions, position)
            frequencies[value] = positions
        }
    }
    return Input{
        lines,
        frequencies,
        maxX,
        maxY,
    }
}

func part1(value string) int {
    input := parse(value)
    antinodes := map[[2]int]string{}
    for value, positions := range input.frequencies {
        if value != "." {
            for _, position := range positions {
                for _, otherPosition := range positions {
                    if position != otherPosition {
                        x, y := position[0], position[1]
                        otherX, otherY := otherPosition[0], otherPosition[1]
                        dX,dY := x - otherX, y - otherY
                        newX,newY  := x + dX, y + dY
                        newPosition := [2]int{newX, newY}
                        if newX >= 0 && newY >= 0 && newX <= input.maxX && newY <= input.maxY {
                            antinodes[newPosition] = "#"
                        }
                    }
                }
            }
        }
    }
    return len(antinodes)
}

func part2(value string) int {
    input := parse(value)
    antinodes := map[[2]int]string{}
    for value, positions := range input.frequencies {
        if value != "." {
            for _, position := range positions {
                for _, otherPosition := range positions {
                    if position != otherPosition {
                        x, y := position[0], position[1]
                        otherX, otherY := otherPosition[0], otherPosition[1]
                        dX,dY := x - otherX, y - otherY
                        newX,newY  := x + dX, y + dY
                        newPosition := [2]int{newX, newY}
                        antinodes[position] = "#"
                        antinodes[otherPosition] = "#"
                        for newX >= 0 && newY >= 0 && newX <= input.maxX && newY <= input.maxY {
                            antinodes[newPosition] = "#"
                            newX, newY = newX + dX, newY + dY
                            newPosition = [2]int{newX, newY}
                        }
                    }
                }
            }
        }
    }
    return len(antinodes)
}

func printGrid(input Input, antinodes map[[2]int]string) {
    for y := 0; y <= input.maxY; y++ {
        fmt.Println()
        for x := 0; x <= input.maxX; x++ {
            if antinodes[[2]int{x, y}] == "#" {
                fmt.Print("#")
            } else {
                value := string(input.lines[y][x])
                fmt.Print(value)
            }
        }
    }
    fmt.Println()
}

func main() {
    println(part1(inputTest))
    println(part1(input))
    println(part2(inputTest))
    println(part2(input))
}
