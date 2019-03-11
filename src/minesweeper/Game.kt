package minesweeper

class Game(fieldSize: FieldSize, mineCount: Int) {
    private val fieldInfo = FieldInfo(fieldSize, mineCount)

    val checkGameToWin: Boolean
        get() = fieldInfo.checkGameToWin

    private val rowIndices: IntRange
        get() = fieldInfo.rowIndices

    private val colIndices: IntRange
        get() = fieldInfo.colIndices

    private var isInitialized: Boolean = false

    private fun initializeGame(startCellPosition: CellPosition) {
        fieldInfo.generateMines(startCellPosition)
        fieldInfo.countMines()
        fieldInfo.openCell(startCellPosition)
    }

    fun openCell(cell: CellPosition): Boolean {
        if (!isInitialized) {
            initializeGame(cell)
            isInitialized = true
            return false
        }

        return fieldInfo.openCell(cell)
    }

    fun markCell(cell: CellPosition) = fieldInfo.mark(cell)

    fun toString(showAllHiddenCells: Boolean) = fieldInfo.toString(showAllHiddenCells)

    fun isValidCellPosition(cell: CellPosition) = cell.rowId in rowIndices && cell.colId in colIndices
}