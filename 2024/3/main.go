package main

import (
	"regexp"
	"strings"
  "strconv"

	_ "embed"
)

//go:embed inputTest.txt
var inputTest string

//go:embed input.txt
var input string


func part1(value string) int {
    r, _ := regexp.Compile(`mul\(\d+,\d+\)`)
    matches := r.FindAllStringSubmatch(value, -1)
    sum := 0
    for _, match := range matches {
        value := match[0]
        value, _ = strings.CutPrefix(value, "mul(")
        value, _ = strings.CutSuffix(value, ")")
        values := strings.Split(value, ",")
        first, _ := strconv.Atoi(values[0])
        second, _ := strconv.Atoi(values[1])
        sum += first * second

    }
    return sum
}

func part2(value string) int {
    r, _ := regexp.Compile(`mul\(\d+,\d+\)|(don\'t)|(do)`)
    matches := r.FindAllStringSubmatch(value, -1)
    sum := 0
    do := true
    for _, match := range matches {
        value := match[0]
        switch value[0:2] {
        case "mu":
            value, _ = strings.CutPrefix(value, "mul(")
            value, _ = strings.CutSuffix(value, ")")
            values := strings.Split(value, ",")
            first, _ := strconv.Atoi(values[0])
            second, _ := strconv.Atoi(values[1])
            if do {
                sum += first*second
            }
        case "do":
            if value == "do" {
                do = true
            } else {
                do = false
            }
        }

    }
    return sum
}


func main() {
    println(part1(inputTest))
    println(part1(input))
    println(part2(inputTest))
    println(part2(input))
}
