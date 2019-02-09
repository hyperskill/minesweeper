package minesweeper

import java.util.*
import kotlin.random.Random

fun generateMines(field: Array<CharArray>, mineCount: Int) {
    var needMine = mineCount
    for (i in 0..9) {
        for (j in 0..9) {
            if (needMine > 0 && (Random.nextInt(100) <= mineCount || needMine == 100 - i * 10 - j)) {
                needMine--
                field[i][j] = 'X'
            } else
                field[i][j] = '.'
        }
    }
}

fun countMines(field: Array<CharArray>) {
    for (i in 0..9)
        for (j in 0..9)
            if (field[i][j] != 'X') {
                val mineCount = countMinesAroundCell(field, i, j)
                if (mineCount != 0)
                    field[i][j] = '0' + mineCount
            }
}

fun countMinesAroundCell(field: Array<CharArray>, i: Int, j: Int): Int {
    val delta = intArrayOf(-1, 0, 1)

    var cnt = 0
    for (dx in delta)
        for (dy in delta)
            if (!(dx == 0 && dy == 0) && i + dx >= 0 && dx + i < 10 && j + dy >= 0 && j + dy < 10 &&
                    field[dx + i][dy + j] == 'X')
                cnt++

    return cnt
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    print("How many mines do you want on the field? ")
    val mineCount = scanner.nextInt()

    val field = Array(10) { CharArray(10) }
    generateMines(field, mineCount)
    countMines(field)

    println(field.joinToString("\n") { it.joinToString("") })
}