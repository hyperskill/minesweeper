package minesweeper

import java.util.*
import kotlin.random.Random

const val MINE_CHAR = 'X'
const val HIDDEN_CELL_CHAR = '.'
const val EMPTY_CELL_CHAR = '\\'
const val MARK_CHAR = '*'

class FieldInfo(
    private val fieldSize: FieldSize,
    val mineCount: Int
) {
    val rowIndices: IntRange
        get() = 0 until fieldSize.rows

    val colIndices: IntRange
        get() = 0 until fieldSize.cols

    val opened = mutableSetOf<CellPosition>()

    val marks = mutableSetOf<CellPosition>()

    val mines = mutableSetOf<CellPosition>()

    val rows = fieldSize.rows

    val cols = fieldSize.cols

    val cells: Iterable<CellPosition>
        get() = object: Iterable<CellPosition> {
            override fun iterator(): Iterator<CellPosition> {
                return object: Iterator<CellPosition> {
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

    val minesAroundCells: Array<IntArray> = Array(rows) { IntArray(cols) }
}


data class FieldSize(val rows: Int, val cols: Int)
data class CellPosition(val rowId: Int, val colId: Int)

fun FieldInfo.generateMines(generationPoint: CellPosition) {
    var remainingMines = mineCount
    for (rowId in rowIndices) {
        for (colId in colIndices) {
            val remainingCellsCount = rows * cols - rowId * rows - colId

            if (rowId == generationPoint.rowId && colId == generationPoint.colId) {
                if (remainingMines >= remainingCellsCount) {
                    val firstNotMine = checkNotNull(findFirstCellNotMine(this)) {
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

fun findFirstCellNotMine(fieldInfo: FieldInfo): CellPosition? {
    for (rowId in fieldInfo.rowIndices) {
        for (colId in fieldInfo.colIndices) {
            val currentCell = CellPosition(rowId, colId)

            if (currentCell !in fieldInfo.mines) {
                return currentCell
            }
        }
    }

    return null
}

fun countMines(fieldInfo: FieldInfo) {
    for (cell in fieldInfo.cells) {
        if (cell !in fieldInfo.mines) {
            fieldInfo.minesAroundCells[cell.rowId][cell.colId] =
                countMinesAroundCell(fieldInfo, cell.rowId, cell.colId)
        }
    }
}

fun countMinesAroundCell(fieldInfo: FieldInfo, rowId: Int, colId: Int): Int {
    val deltas = intArrayOf(-1, 0, 1)

    var cnt = 0
    for (dRowId in deltas) {
        for (dColId in deltas) {
            if (!(dRowId == 0 && dColId == 0) &&
                rowId + dRowId in fieldInfo.rowIndices &&
                colId + dColId in fieldInfo.colIndices &&
                CellPosition(rowId + dRowId, colId + dColId) in fieldInfo.mines
            ) {
                cnt++
            }
        }
    }

    return cnt
}

fun colIdToString(id: Int) = "${'a' + id}"

fun printField(fieldInfo: FieldInfo, showAllHiddenCells: Boolean) {
    val fieldToPrint = Array(fieldInfo.rows) { CharArray(fieldInfo.cols) }

    for (cell in fieldInfo.cells) {
        fieldToPrint[cell.rowId][cell.colId] = when (cell) {
            in fieldInfo.mines -> MINE_CHAR
            else ->
                if (fieldInfo.minesAroundCells[cell.rowId][cell.colId] != 0)
                    '0' + fieldInfo.minesAroundCells[cell.rowId][cell.colId]
                else
                    EMPTY_CELL_CHAR
        }
    }

    if (!showAllHiddenCells) {
        for (cell in fieldInfo.cells) {
            fieldToPrint[cell.rowId][cell.colId] = when (cell) {
                in fieldInfo.marks -> MARK_CHAR
                !in fieldInfo.opened -> HIDDEN_CELL_CHAR
                else -> fieldToPrint[cell.rowId][cell.colId]
            }
        }
    }

    val indent = fieldInfo.cols.toString().length
    println("${" ".repeat(indent)}|${fieldInfo.rowIndices.joinToString("") { colIdToString(it) }}|")
    println("${"-".repeat(indent)}|${"-".repeat(fieldInfo.rows)}|")

    println(fieldToPrint.withIndex().joinToString("\n") { (rowId, row) ->
        "%${indent}d".format(rowId + 1) + row.joinToString(separator = "", prefix = "|", postfix = "|")
    })

    println("${"-".repeat(indent)}|${"-".repeat(fieldInfo.rows)}|")
}

fun openCell(fieldInfo: FieldInfo, cell: CellPosition): Boolean {
    if (cell in fieldInfo.opened) {
        return false
    }

    if (cell in fieldInfo.mines) {
        return true
    }

    if (cell in fieldInfo.marks) {
        fieldInfo.marks -= cell
    }

    fieldInfo.opened += cell
    if (fieldInfo.minesAroundCells[cell.rowId][cell.colId] == 0) {
        openNeighbours(fieldInfo, cell)
    }

    return false
}

fun openNeighbours(fieldInfo: FieldInfo, cell: CellPosition) {
    val deltas = intArrayOf(-1, 0, 1)

    for (dRowId in deltas) {
        for (dColId in deltas) {
            if (cell.rowId + dRowId !in fieldInfo.rowIndices ||
                cell.colId + dColId !in fieldInfo.colIndices
            ) {
                continue
            }

            val newCellToOpen = CellPosition(cell.rowId + dRowId, cell.colId + dColId)
            if (newCellToOpen !in fieldInfo.opened) {
                openCell(fieldInfo, newCellToOpen)
            }
        }
    }
}

fun checkGameToWin(fieldInfo: FieldInfo): Boolean {
    if (fieldInfo.opened.size == fieldInfo.rows * fieldInfo.cols - fieldInfo.mineCount) {
        return true
    }

    if (fieldInfo.marks.size != fieldInfo.mineCount) {
        return false
    }

    for (markedCell in fieldInfo.marks) {
        if (markedCell !in fieldInfo.cells) {
            return false
        }
    }

    return true
}

fun mark(fieldInfo: FieldInfo, cell: CellPosition) {
    if (cell !in fieldInfo.opened) {
        fieldInfo.marks += cell
    }
}

const val FIELD_SIZE = 10
const val MAX_MINES_COUNT = FIELD_SIZE * FIELD_SIZE - 1
val WHITESPACES = "[\\t ]+".toRegex()

enum class Command(private val stringValue: String) {
    FREE("free"),
    MARK("mark");

    override fun toString() = stringValue
}

data class UserInput(val cell: CellPosition, val cmd: String?)

fun Scanner.readUserCommand(msg: String): UserInput? {
    var cmd: String? = null
    var rowId: Int? = null
    var colId: Int? = null

    do {
        print(msg)

        val enteredValues = WHITESPACES.split(nextLine() ?: return null)

        if (enteredValues.size < 2 || enteredValues[0].isEmpty() || enteredValues[1].isEmpty())
            continue

        rowId = enteredValues[0].toIntOrNull() ?: enteredValues[1].toIntOrNull()

        colId = when {
            enteredValues[1].first() in 'a'..'z' -> enteredValues[1].first() - 'a'
            enteredValues[0].first() in 'a'..'z' -> enteredValues[0].first() - 'a'
            else -> null
        }

        if (enteredValues.size > 2)
            cmd = enteredValues[2]

    } while (rowId == null || colId == null)

    return UserInput(CellPosition(rowId - 1, colId), cmd)
}

fun main() {
    val scanner = Scanner(System.`in`)
    var mineCount: Int

    do {
        print("How many mines do you want on the field(from 1 to $MAX_MINES_COUNT)? ")
        mineCount = scanner.nextInt()
    } while (mineCount !in 1..MAX_MINES_COUNT)

    val fieldInfo = FieldInfo(FieldSize(FIELD_SIZE, FIELD_SIZE), mineCount)

    printField(fieldInfo, false)
    var generationPoint: CellPosition
    do {
        val (cell, _) = scanner.readUserCommand("Choose first cell to free: ") ?: return
        generationPoint = cell
    } while (generationPoint.rowId !in fieldInfo.rowIndices ||
        generationPoint.colId !in fieldInfo.colIndices
    )

    fieldInfo.generateMines(generationPoint)
    countMines(fieldInfo)
    openCell(fieldInfo, generationPoint)
    printField(fieldInfo, false)

    var isUserFail = false
    while (!checkGameToWin(fieldInfo) && !isUserFail) {
        val (cell, cmd) = scanner.readUserCommand(
            "Set/unset mines marks or claim a cell as free(coordinates and command): "
        ) ?: return

        println("$cell $cmd")

        if (cell.rowId in fieldInfo.rowIndices && cell.rowId in fieldInfo.rowIndices) {
            isUserFail = when (cmd) {
                Command.FREE.toString() -> openCell(fieldInfo, cell)
                Command.MARK.toString() -> {
                    mark(fieldInfo, cell)
                    false
                }
                else -> false
            }
        }
        printField(fieldInfo, isUserFail)
    }

    if (!isUserFail)
        println("Congratulations! You found all mines!")
    else
        println("You stepped on a mine and failed!")
}