package minesweeper

class Game(fieldSize: FieldSize, mineCount: Int) {
    private val fieldInfo = FieldInfo(fieldSize, mineCount)

    val checkGameToWin: Boolean
        get() = fieldInfo.checkGameToWin

    val rowIndices: IntRange
        get() = fieldInfo.rowIndices

    val colIndices: IntRange
        get() = fieldInfo.colIndices

    fun initializeGame(startCellPosition: CellPosition) {
        fieldInfo.generateMines(startCellPosition)
        fieldInfo.countMines()
        fieldInfo.openCell(startCellPosition)
    }

    fun openCell(cell: CellPosition) = fieldInfo.openCell(cell)

    fun markCell(cell: CellPosition) = fieldInfo.mark(cell)

    fun printField(showAllHiddenCells: Boolean) = fieldInfo.printField(showAllHiddenCells)
}