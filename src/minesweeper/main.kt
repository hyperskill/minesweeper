package minesweeper

import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)
    print("How many mines do you want on the field? ")
    val numberOfMines = scanner.nextInt()
    val board = MinesweeperBoard(numberOfMines = numberOfMines)
    println("\n$board")
}
