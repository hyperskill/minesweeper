package minesweeper

import java.util.*

inline fun <reified T> matrix2d(height: Int, width: Int, initialize: () -> T) =
    Array(height) { Array(width) { initialize() } }

fun main() {
    val numOfMines = getNumberOfMines()
    val game = Minesweeper(numOfMines)
    game.printField()
    while (!game.allMinesFound(numOfMines) && !game.isLost()) {
        makeNextStep(game)
        game.printField()
    }
    if (game.isLost()) {
        println("You stepped on a mine and failed!")
    } else {
        println("Congratulations! You founded all mines!")

    }
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

private fun makeNextStep(game: Minesweeper) {
    println("Set/unset mines marks or claim a cell as free:")
    val scanner = Scanner(System.`in`)
    val y = scanner.nextInt() - 1
    val x = scanner.nextInt() - 1
    if (x in 0..8 && y in 0..8) {
        val nextStep = scanner.next()
        when (nextStep) {
            "free" -> game.checkMine(Pair(x, y))
            "mine" -> game.setMine(Pair(x, y))
        }
    } else {
        println("Please enter correct coordinates (both in range 1..9)")
    }
}

class Minesweeper(numOfMines: Int) {

    init {
        field = generateFieldWithMines(numOfMines)
    }

    companion object {
        const val sizeOfField = 9
        var lost = false
        var correctMinesFound = 0
        var cellsOpened = 0
        var field = matrix2d(sizeOfField, sizeOfField) { MyPair('/', '.') }
    }

    fun isLost() = lost

    fun allMinesFound(numOfMines: Int) = (numOfMines == correctMinesFound)
            || allClosedCellsAreWithMines(numOfMines)

    private fun allClosedCellsAreWithMines(numOfMines: Int) =
        sizeOfField * sizeOfField - cellsOpened - correctMinesFound == numOfMines

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
                        field[i][j].exactValue = numOfMinesNearby.toString()[0]
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

    fun checkMine(coordinates: Pair<Int, Int>) {
        val x = coordinates.first
        val y = coordinates.second
        if (field[x][y].exactValue == 'X') {
            lost = true
            field[x][y].playerValue = 'X'
        } else {
            openAllPossibleCellsNearby(coordinates)
        }
    }

    fun setMine(coordinates: Pair<Int, Int>) {
        val x = coordinates.first
        val y = coordinates.second
        if (field[x][y].exactValue == 'X') {
            if (field[x][y].playerValue == '.') {
                field[x][y].playerValue = '*'
                correctMinesFound++
                cellsOpened++
            } else {
                field[x][y].playerValue = '.'
                correctMinesFound--
                cellsOpened--
            }
        } else if (field[x][y].playerValue == '.') {
            field[x][y].playerValue = '*'
            cellsOpened++
        } else if (field[x][y].playerValue == '*') {
            field[x][y].playerValue = '.'
            cellsOpened--
        }
    }

    private fun openAllPossibleCellsNearby(coordinates: Pair<Int, Int>) {
        val queue: Queue<Pair<Int, Int>> = ArrayDeque<Pair<Int, Int>>()
        val first = coordinates.first
        val second = coordinates.second
        queue.add(Pair(first, second))
        while (!queue.isEmpty()) {
            val element = queue.remove()
            val x = element.first
            val y = element.second
            when (field[x][y].exactValue) {
                in '1'..'8' -> {
                    if (field[x][y].playerValue == '.') {
                        field[x][y].playerValue = field[x][y].exactValue
                        cellsOpened++
                    }
                }
                '/' -> {
                    for (i in -1..1) {
                        for (j in -1..1) {
                            if (correctCoordinates(x + i, y + j) && field[x + i][y + j].playerValue == '.') {
                                field[x + i][y + j].playerValue = field[x + i][y + j].exactValue
                                cellsOpened++
                                if (field[x + i][y + j].exactValue == '/') {
                                    queue.add(Pair(x + i, y + j))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    class MyPair(var exactValue: Char, var playerValue: Char)

}