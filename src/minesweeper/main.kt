package minesweeper

import java.util.Scanner
import java.util.Random

const val SIZE = 9
const val SPACE = '.'
const val MINE = 'X'
var random = Random()

fun generateMap(map: Array<Array<Char>>, n: Int) {
    var x: Int
    var y: Int
    for (i in 1..n) {
        do {
            x = random.nextInt(SIZE) + 1
            y = random.nextInt(SIZE) + 1
        } while (map[x][y] == MINE)
        map[x][y] = MINE
    }

}

fun calculateMap(map: Array<Array<Char>>) {
    val indexes = arrayOf(-1, 0, 1)
    for (i in 1..SIZE)
        for (j in 1..SIZE)
            if (map[i][j] == SPACE) {
                var sum = 0
                for (k in indexes)
                    for (p in indexes)
                        if (map[i + k][p + j] == MINE)
                            sum++
                if (sum != 0)
                    map[i][j] = sum.toString().first()
            }
}

fun printMap(map: Array<Array<Char>>) {
    println()
    for (i in map.slice(1..SIZE)) {
        println(i.joinToString(separator = "").substring(1, SIZE + 1))
    }
}

fun main(args: Array<String>) {
    val map: Array<Array<Char>> = Array(SIZE + 2, { Array(SIZE + 2, { SPACE }) })
    val scanner = Scanner(System.`in`)
    var n: Int
    do {
        print("How many mines do you want on the field? ")
        n = scanner.nextInt()
    } while (n < 0 || n > SIZE * SIZE)
    generateMap(map, n)
    calculateMap(map)
    printMap(map)
}
