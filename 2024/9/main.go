package main

import (
	"github.com/samber/lo"
	_ "embed"
	"fmt"
	"slices"
	"strconv"
	"strings"
)

//go:embed inputTest.txt
var inputTest string

//go:embed input.txt
var input string

type Input struct {
    intList []int
}

func parse(value string) Input {
    line := strings.Split(value, "\n")[0]
    intList:= []int{}
    for _, char := range line {
        value := int(char - '0')
        intList = append(intList, value)
    }
    return Input{
        intList,
    }
}

func part1(input Input) int {
    blocks := []string{}
    char := "."
    for i, value := range input.intList {
        char = "."
        if i%2 == 0 {
            char = strconv.Itoa(i/2)
        }
        for j := 1; j <= value; j++ {
            blocks = append(blocks, char)
        }
    }

    condition := true
    newBlocks := blocks
    for condition == true {
        for i := len(blocks) - 1; i >= 0; i-- {
            value := blocks[i]
            if (value != ".") {
                for j := 0; j <= len(newBlocks)-1; j++ {
                    newValue := newBlocks[j]
                    if (j == i) {
                        condition = false
                    }
                    if (newValue == ".") {
                        newBlocks[i] = "."
                        newBlocks[j] = value
                        j = len(newBlocks) + 1
                    }
                }
            }
        } 
    }
    sum := 0
    for i, value := range newBlocks {
        intValue, _ := strconv.Atoi(value)
        sum += (i-1)*intValue    
    }
    return sum
}

type block struct {
    size int
    value string
}

func part2(input Input) int {
    blocks := []block{}
    char := "."
    for i, value := range input.intList {
        char = "."
        if i%2 == 0 {
            char = strconv.Itoa(i/2)
        }
        blocks = append(blocks, block{value, char})
    }

    newBlocks := blocks
    for i := len(blocks) - 1; i >= 0 ; i-- {
        b := blocks[i]
        if (b.value != ".") {
            condition := true
            for j := 0; j <= len(blocks) - 1 && j <= i && condition; j++ {
                otherBlock :=  blocks[j]
                if (otherBlock.size >= b.size && otherBlock.value == ".") {
                    newBlocks[j] = b
                    newBlocks[i] = block{b.size, "."}
                    if (otherBlock.size != b.size) {
                        newBlocks = slices.Insert(newBlocks, j+1, block{otherBlock.size - b.size, otherBlock.value})
                    }
                    condition = false
                }
            }
        }
    }
    newBlocksFlatten := lo.FlatMap(newBlocks, func(b block, _ int) []string {
        return lo.Times(b.size, func(_ int) string{
            return b.value
        })
    })

    return lo.Reduce(newBlocksFlatten, func(acc int, value string, i int) int {
        intValue, _ := strconv.Atoi(value)
        return acc + i*intValue
    }, 0)
}


func printBlocks(blocks []block) {
    for _, b := range blocks {
        for j := 0; j < b.size; j++ {
            fmt.Printf("%v", b.value)
        }
    }
    fmt.Println()
}

func main() {
    println(part1(parse(inputTest)))
    println(part1(parse(input)))
    result := part2(parse(inputTest))
    println()
    println("Result part 2 input test")
    println(result)
    println()
    println("Result part 2 input")
    result = part2(parse(input))
    println(result)
}
