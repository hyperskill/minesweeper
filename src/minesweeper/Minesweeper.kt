package minesweeper

import java.util.*

fun main(args: Array<String>) {
    val numOfMines = getNumberOfMines()
    val game = Minesweeper(numOfMines)
    game.printField()
}

private fun getNumberOfMines(): Int {
    print("How many mines do you want on the field? ")
    val scanner = Scanner(System.`in`)
    var numOfMines = scanner.nextInt()
    while (numOfMines > 81 || numOfMines < 1) {
        println()
        print("Number of mines should be positive number not bigger than 81. Enter new number.")
        numOfMines = scanner.nextInt()
    }
    return numOfMines

}

class Minesweeper(numOfMines: Int) {

    init {
        field = generateFieldWithMines(numOfMines)
    }

    companion object {
        const val sizeOfField = 9
        var field: Array<CharArray> = Array(sizeOfField, { CharArray(sizeOfField) })
    }

    private fun generateMines(numOfMines: Int): MutableList<Boolean> {
        val mines: MutableList<Boolean> = mutableListOf()
        for (i in 0 until numOfMines) {
            mines.add(true)
        }
        for (i in numOfMines until sizeOfField * sizeOfField) {
            mines.add(false)
        }
        mines.shuffle()
        return mines
    }

    private fun generateFieldWithMines(numOfMines: Int): Array<CharArray> {
        val mines = generateMines(numOfMines)

        for (i in 0 until sizeOfField) {
            for (j in 0 until sizeOfField) {
                if (mines[i * sizeOfField + j]) {
                    field[i][j] = 'X'
                } else {
                    field[i][j] = '.'
                }
            }
        }
        return field
    }

    fun printField() {
        for (line in field) {
            for (cell in line) {
                print("$cell ")
            }
            println()
        }
    }

}