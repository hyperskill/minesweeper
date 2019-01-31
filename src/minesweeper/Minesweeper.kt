package minesweeper

import java.util.*

inline fun <reified T> matrix2d(height: Int, width: Int, initialize: () -> T) =
    Array(height) { Array(width) { initialize() } }

fun main() {
    val numOfMines = getNumberOfMines()
    val game = Minesweeper(numOfMines)
    game.printField()
    while (!game.allMinesFound(numOfMines)) {
        val coordinates = readCoordinates()
        game.placeMarkOnCoordinates(coordinates)
        game.printField()
    }
    println("Congratulations! You founded all mines!")
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

private fun readCoordinates(): Pair<Int, Int> {
    while (true) {
        println("Set/delete mines marks (x and y coordinates): ")
        val scanner = Scanner(System.`in`)
        val x = scanner.nextInt()
        val y = scanner.nextInt()
        if (x in 1..9 && y in 1..9) {
            return Pair(x, y)
        } else {
            println("Please enter correct coordinates (both in range 1..9)")
        }
    }
}

class Minesweeper(numOfMines: Int) {

    init {
        field = generateFieldWithMines(numOfMines)
    }

    companion object {
        const val sizeOfField = 9
        var correctMinesFound = 0
        var totalMinesSet = 0
        var field = matrix2d(sizeOfField, sizeOfField) { MyPair('.', '.') }
    }

    fun allMinesFound(numOfMines: Int) = numOfMines == correctMinesFound && numOfMines == totalMinesSet

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

    private fun generateFieldWithMines(numOfMines: Int): Array<Array<MyPair>> {
        val mines = generateMines(numOfMines)
        placeMinesToField(mines)
        calculateAmountOfMinesAroundEmptyCells()
        return field
    }

    private fun placeMinesToField(mines: MutableList<Boolean>) {
        for (i in 0 until sizeOfField) {
            for (j in 0 until sizeOfField) {
                if (mines[i * sizeOfField + j]) {
                    field[i][j].exactValue = 'X'
                }
            }
        }
    }

    private fun calculateAmountOfMinesAroundEmptyCells() {
        for (i in 0 until sizeOfField) {
            for (j in 0 until sizeOfField) {

                var numOfMinesNearby = 0
                if (field[i][j].exactValue != 'X') {
                    for (k in -1..1) {
                        for (l in -1..1) {
                            if (correctCoordinates(i + k, j + l) && field[i + k][j + l].exactValue == 'X') {
                                numOfMinesNearby++
                            }
                        }
                    }
                    if (numOfMinesNearby > 0) {
                        field[i][j].playerValue = numOfMinesNearby.toString()[0]
                    }
                }
            }
        }
    }

    private fun correctCoordinates(i: Int, j: Int) =
        i in 0..(sizeOfField - 1) && j in 0..(sizeOfField - 1)

    fun printField() {
        println("\n │123456789│")
        println("—│—————————│")
        var index = 1
        for (line in field) {
            print("$index|")
            for (cell in line) {
                print("${cell.playerValue}")
            }
            println("|")
            index++
        }
        println("—│—————————│")
    }

    fun placeMarkOnCoordinates(coordinates: Pair<Int, Int>) {
        val y = coordinates.first - 1
        val x = coordinates.second - 1
        if (field[x][y].exactValue == 'X') {
            if (field[x][y].playerValue == '.') {
                field[x][y].playerValue = '*'
                correctMinesFound++
                totalMinesSet++
            } else {
                field[x][y].playerValue = '.'
                correctMinesFound--
                totalMinesSet--
            }
        } else if (field[x][y].playerValue == '.') {
            field[x][y].playerValue = '*'
            totalMinesSet++
        } else if (field[x][y].playerValue == '*') {
            field[x][y].playerValue = '.'
            totalMinesSet--
        }
    }

    class MyPair(var exactValue: Char, var playerValue: Char)

}