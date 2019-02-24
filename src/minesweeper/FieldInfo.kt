package minesweeper

import kotlin.random.Random

const val MINE_CHAR = 'X'
const val HIDDEN_CELL_CHAR = '.'
const val EMPTY_CELL_CHAR = '\\'
const val MARK_CHAR = '*'

class FieldInfo(
    private val fieldSize: FieldSize,
    private val mineCount: Int
) {
    val rowIndices: IntRange
        get() = 0 until fieldSize.rows

    val colIndices: IntRange
        get() = 0 until fieldSize.cols

    private val opened = mutableSetOf<CellPosition>()

    private val marks = mutableSetOf<CellPosition>()

    private val mines = mutableSetOf<CellPosition>()

    val rows = fieldSize.rows

    val cols = fieldSize.cols

    private val cells: Iterable<CellPosition>
        get() = object : Iterable<CellPosition> {
            override fun iterator(): Iterator<CellPosition> {
                return object : Iterator<CellPosition> {
                    var currentCell = CellPosition(0, -1)

                    override fun hasNext(): Boolean {
                        return currentCell.rowId != rows - 1 || currentCell.colId != cols - 1
                    }

                    override fun next(): CellPosition {
                        var colId = currentCell.colId + 1
                        val rowId: Int

                        if (colId == cols) {
                            colId = 0
                            rowId = currentCell.rowId + 1
                        } else {
                            rowId = currentCell.rowId
                        }

                        currentCell = CellPosition(rowId, colId)

                        return currentCell
                    }
                }
            }
        }

    private val minesAroundCells: Array<IntArray> = Array(rows) { IntArray(cols) }

    val checkGameToWin: Boolean
        get() {
            if (opened.size == rows * cols - mineCount) {
                return true
            }

            if (marks.size != mineCount) {
                return false
            }

            for (markedCell in marks) {
                if (markedCell !in cells) {
                    return false
                }
            }

            return true
        }

    fun generateMines(generationPoint: CellPosition) {
        var remainingMines = mineCount
        for (rowId in rowIndices) {
            for (colId in colIndices) {
                val remainingCellsCount = rows * cols - rowId * rows - colId

                if (rowId == generationPoint.rowId && colId == generationPoint.colId) {
                    if (remainingMines >= remainingCellsCount) {
                        val firstNotMine = checkNotNull(findFirstCellNotMine()) {
                            "Internal error! Field with size: ($rows, $cols)" +
                                    " cannot contain $mineCount mines"
                        }
                        mines += CellPosition(firstNotMine.rowId, firstNotMine.colId)
                        remainingMines--
                    }
                    continue
                }

                if (remainingMines > 0 && (Random.nextInt(rows * cols - 1) <= mineCount ||
                            remainingMines == remainingCellsCount)
                ) {
                    remainingMines--
                    mines += CellPosition(rowId, colId)
                }
            }
        }
    }

    private fun findFirstCellNotMine(): CellPosition? {
        for (rowId in rowIndices) {
            for (colId in colIndices) {
                val currentCell = CellPosition(rowId, colId)

                if (currentCell !in mines) {
                    return currentCell
                }
            }
        }

        return null
    }

    fun countMines() {
        for (cell in cells) {
            if (cell !in mines) {
                minesAroundCells[cell.rowId][cell.colId] =
                    countMinesAroundCell(cell.rowId, cell.colId)
            }
        }
    }

    private fun countMinesAroundCell(rowId: Int, colId: Int): Int {
        val deltas = intArrayOf(-1, 0, 1)

        var cnt = 0
        for (dRowId in deltas) {
            for (dColId in deltas) {
                if (!(dRowId == 0 && dColId == 0) &&
                    rowId + dRowId in rowIndices &&
                    colId + dColId in colIndices &&
                    CellPosition(rowId + dRowId, colId + dColId) in mines
                ) {
                    cnt++
                }
            }
        }

        return cnt
    }

    private fun colIdToString(id: Int) = "${'a' + id}"

    fun printField(showAllHiddenCells: Boolean) {
        val fieldToPrint = Array(rows) { CharArray(cols) }

        for (cell in cells) {
            fieldToPrint[cell.rowId][cell.colId] = when (cell) {
                in mines -> MINE_CHAR
                else ->
                    if (minesAroundCells[cell.rowId][cell.colId] != 0)
                        '0' + minesAroundCells[cell.rowId][cell.colId]
                    else
                        EMPTY_CELL_CHAR
            }
        }

        if (!showAllHiddenCells) {
            for (cell in cells) {
                fieldToPrint[cell.rowId][cell.colId] = when (cell) {
                    in marks -> MARK_CHAR
                    !in opened -> HIDDEN_CELL_CHAR
                    else -> fieldToPrint[cell.rowId][cell.colId]
                }
            }
        }

        val indent = cols.toString().length
        println("${" ".repeat(indent)}|${rowIndices.joinToString("") { colIdToString(it) }}|")
        println("${"-".repeat(indent)}|${"-".repeat(rows)}|")

        println(fieldToPrint.withIndex().joinToString("\n") { (rowId, row) ->
            "%${indent}d".format(rowId + 1) + row.joinToString(separator = "", prefix = "|", postfix = "|")
        })

        println("${"-".repeat(indent)}|${"-".repeat(rows)}|")
    }

    fun openCell(cell: CellPosition): Boolean {
        if (cell in opened) {
            return false
        }

        if (cell in mines) {
            return true
        }

        if (cell in marks) {
            marks -= cell
        }

        opened += cell
        if (minesAroundCells[cell.rowId][cell.colId] == 0) {
            openNeighbours(cell)
        }

        return false
    }

    private fun openNeighbours(cell: CellPosition) {
        val deltas = intArrayOf(-1, 0, 1)

        for (dRowId in deltas) {
            for (dColId in deltas) {
                if (cell.rowId + dRowId !in rowIndices ||
                    cell.colId + dColId !in colIndices
                ) {
                    continue
                }

                val newCellToOpen = CellPosition(cell.rowId + dRowId, cell.colId + dColId)
                if (newCellToOpen !in opened) {
                    openCell(newCellToOpen)
                }
            }
        }
    }

    fun mark(cell: CellPosition) {
        if (cell !in opened) {
            marks += cell
        }
    }
}

data class FieldSize(val rows: Int, val cols: Int)
data class CellPosition(val rowId: Int, val colId: Int)