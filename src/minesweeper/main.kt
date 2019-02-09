package minesweeper

import java.util.*
import kotlin.random.Random

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    print("How many mines do you want on the field? ")
    val mineCount = scanner.nextInt()

    var needMine = mineCount
    for (i in 0..9) {
        for (j in 0..9) {
            if (needMine > 0 && (Random.nextInt(100) <= mineCount || needMine == 100 - i * 10 - j)) {
                needMine--
                print("X")
            } else
                print(".")
        }
        println()
    }
}