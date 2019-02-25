package minesweeper

import java.util.Scanner
import java.util.Random

var random = Random()

fun main(args: Array<String>) {
    val len = 9
    val map: Array<Array<Char>> = Array(len, { Array(len, { '.' }) })
    val scanner = Scanner(System.`in`)
    var x: Int
    var y: Int
    var n: Int
    do {
        print("How many mines do you want on the field? ")
        n = scanner.nextInt()
    } while (n < 0 || n > len * len)
    for (i in 1..n) {
        do {
            x = random.nextInt(len)
            y = random.nextInt(len)
        } while (map[x][y] == 'X')
        map[x][y] = 'X'
    }
    println()
    for (i in map) {
        println(i.joinToString(separator = ""))
    }
}
