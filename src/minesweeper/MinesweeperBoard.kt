package minesweeper

import kotlin.random.Random

/**
 * A board for the *Minesweeper* game has a size of [m][numberOfRows]-by-[n][numberOfColumns]
 * horizontally and vertically respectively (in cells) and a specified [number][numberOfMines] of mines.
 * By default, a nine-by-nine board is created, and there is no mines on it.
 */
class MinesweeperBoard(
    val numberOfRows: Int = 9, val numberOfColumns: Int = 9,
    val numberOfMines: Int = 0
) {
    val numberOfCells get() = numberOfColumns * numberOfRows

    private val cells: Array<CharArray>

    init {
        checkPositiveBoardSize(numberOfRows, numberOfColumns)
        checkNumberOfMinesInRange(numberOfMines)
        cells = if (numberOfMines <= numberOfCells / 2) {
            generatedCells(SAFE_CELL, MINE_CELL, numberOfMines)
        } else {
            generatedCells(MINE_CELL, SAFE_CELL, numberOfCells - numberOfMines)
        }
    }

    // Constructor's inner methods

    private fun checkPositiveBoardSize(rows: Int, columns: Int) {
        if (rows <= 0 || columns <= 0) {
            throw java.lang.IllegalArgumentException(
                "The dimensions of the board should be positive. Got $rows rows and $columns columns."
            )
        }
    }

    private fun checkNumberOfMinesInRange(number: Int) {
        if (number < 0) {
            throw java.lang.IllegalArgumentException("The given number of mines is negative: $number.")
        }
        if (number > numberOfCells) {
            throw IllegalArgumentException(
                "The given number of mines is greater than the number of the cells's cells: $number > $numberOfCells."
            )
        }
    }

    private fun generatedCells(
        initContent: Char,
        generatedContent: Char, numberOfGeneratedContent: Int
    ): Array<CharArray> {
        val cells = Array(numberOfColumns) { CharArray(numberOfRows) { initContent } }
        var count = 0
        while (count < numberOfGeneratedContent) {
            val row = Random.nextInt(0, numberOfRows)
            val column = Random.nextInt(0, numberOfColumns)
            if (cells[row][column] == initContent) {
                cells[row][column] = generatedContent
                cells.setWarningsAroundMineAt(row, column)
                count += 1
            }
        }
        return cells
    }

    private fun Array<CharArray>.setWarningsAroundMineAt(row: Int, column: Int) {
        if (this[row][column] != MINE_CELL) return
        for (direction in Direction.values()) {
            setWarningInDirection(direction, row, column)
        }
    }

    enum class Direction(val shiftRow: Int, val shiftColumn: Int) {
        UP(-1, 0), UP_RIGHT(-1, 1),
        RIGHT(0, 1), DOWN_RIGHT(1, 1),
        DOWN(1, 0), DOWN_LEFT(1, -1),
        LEFT(0, -1), UP_LEFT(-1, -1);
    }

    private fun Array<CharArray>.setWarningInDirection(direction: Direction, row: Int, column: Int) {
        val r = row + direction.shiftRow
        val c = column + direction.shiftColumn
        if (r < 0 || r >= numberOfRows || c < 0 || c >= numberOfColumns) return
        if (this[r][c] == MINE_CELL) return
        if (this[r][c] == SAFE_CELL) {
            this[r][c] = '1'
        } else {
            this[r][c] = this[r][c] + 1
        }
    }

    // String representation

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
