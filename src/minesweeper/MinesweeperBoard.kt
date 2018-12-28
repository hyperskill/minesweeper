package minesweeper

import kotlin.random.Random

/**
 * A board for the *Minesweeper* game has a size of [m][horizontalSize]-by-[n][verticalSize]
 * horizontally and vertically respectively (in cells) and a specified [number][numberOfMines] of mines.
 * By default, a nine-by-nine board is created, and there is no mines on it.
 */
class MinesweeperBoard(
    val horizontalSize: Int = 9, val verticalSize: Int = 9,
    val numberOfMines: Int = 0
) {
    private val cells: Array<CharArray>

    val numberOfCells get() = horizontalSize * verticalSize

    init {
        checkNumberOfMine(numberOfMines)
        cells = if (numberOfMines <= numberOfCells / 2) {
            generatedCells(SAFE_CELL, MINE_CELL, numberOfMines)
        } else {
            generatedCells(MINE_CELL, SAFE_CELL, numberOfCells - numberOfMines)
        }
    }

    // Constructor's inner method
    private fun checkNumberOfMine(number: Int) {
        if (number < 0) {
            throw java.lang.IllegalArgumentException("The given number of mines is negative: $number.")
        }
        if (number > numberOfCells) {
            throw IllegalArgumentException(
                "The given number of mines is greater than the number of the cells's cells: $number > $numberOfCells"
            )
        }
    }

    // Constructor's inner method
    private fun generatedCells(
        initContent: Char,
        generatedContent: Char, numberOfGeneratedContent: Int
    ): Array<CharArray> {
        val cells = Array(horizontalSize) { CharArray(verticalSize) { initContent } }
        var count = 0
        while (count < numberOfGeneratedContent) {
            val x = Random.nextInt(0, horizontalSize)
            val y = Random.nextInt(0, verticalSize)
            if (cells[y][x] == initContent) {
                cells[y][x] = generatedContent
                count += 1
            }
        }
        return cells
    }

    override fun toString() =
        cells.joinToString("\n") { row ->
            row.joinToString(" ")
        }

    companion object {

        // Marking a safe cell and a cell with a mine

        const val SAFE_CELL = '⋅'
        const val MINE_CELL = '×'
    }
}
