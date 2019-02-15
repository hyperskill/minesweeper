package minesweeper

import java.util.*
import kotlin.random.Random

const val MINE_CHAR = 'X'
const val HIDDEN_CELL_CHAR = '.'
const val EMPTY_CELL_CHAR = '\\'
const val MARK_CHAR = '*'

class FieldInfo(
    val field: Array<CharArray>,
    val mineCount: Int,
    val marks: MutableSet<Pair<Int, Int>>,
    val opened: MutableSet<Pair<Int, Int>>
)

fun generateMines(fieldInfo: FieldInfo, generationPoint: Pair<Int, Int>) {
    var remainingMines = fieldInfo.mineCount
    for (rowId in fieldInfo.field.indices) {
        for (colId in fieldInfo.field[0].indices) {
            val remainingCellsCount = fieldInfo.field.size * fieldInfo.field[0].size -
                    rowId * fieldInfo.field.size - colId

            if (rowId == generationPoint.first && colId == generationPoint.second) {
                if (remainingMines >= remainingCellsCount) {
                    val firstNotMine = findFirstNoMine(fieldInfo)
                    fieldInfo.field[firstNotMine.first][firstNotMine.second] = MINE_CHAR
                    remainingMines--
                }
                continue
            }

            if (remainingMines > 0 &&
                (Random.nextInt(fieldInfo.field.size * fieldInfo.field[0].size - 1) <= fieldInfo.mineCount ||
                        remainingMines == remainingCellsCount)
            ) {
                remainingMines--
                fieldInfo.field[rowId][colId] = MINE_CHAR
            } else
                fieldInfo.field[rowId][colId] = HIDDEN_CELL_CHAR
        }
    }
}

fun findFirstNoMine(fieldInfo: FieldInfo): Pair<Int, Int> {
    for (rowId in fieldInfo.field.indices)
        for (colId in fieldInfo.field[0].indices)
            if (fieldInfo.field[rowId][colId] != MINE_CHAR)
                return Pair(rowId, colId)

    return Pair(-1, -1)
}

fun countMines(fieldInfo: FieldInfo) {
    for (rowId in fieldInfo.field.indices)
        for (colId in fieldInfo.field[0].indices)
            if (fieldInfo.field[rowId][colId] != MINE_CHAR) {
                val mineCount = countMinesAroundCell(fieldInfo, rowId, colId)
                if (mineCount != 0)
                    fieldInfo.field[rowId][colId] = '0' + mineCount
                else
                    fieldInfo.field[rowId][colId] = EMPTY_CELL_CHAR
            }
}

fun countMinesAroundCell(fieldInfo: FieldInfo, rowId: Int, colId: Int): Int {
    val deltas = intArrayOf(-1, 0, 1)

    var cnt = 0
    for (dRowId in deltas)
        for (dColId in deltas)
            if (!(dRowId == 0 && dColId == 0) &&
                rowId + dRowId in fieldInfo.field.indices &&
                colId + dColId in fieldInfo.field[0].indices &&
                fieldInfo.field[dRowId + rowId][dColId + colId] == MINE_CHAR
            ) {
                cnt++
            }

    return cnt
}

fun printField(fieldInfo: FieldInfo, showAllCells: Boolean) {
    val fieldToPrint = fieldInfo.field.map { it.copyOf() }.toTypedArray()

    if (!showAllCells) {
        for (rowId in fieldInfo.field.indices)
            for (colId in fieldInfo.field[0].indices)
                if (Pair(rowId, colId) !in fieldInfo.opened)
                    fieldToPrint[rowId][colId] = HIDDEN_CELL_CHAR

        for ((rowId, colId) in fieldInfo.marks) {
            fieldToPrint[rowId][colId] = MARK_CHAR
        }
    }

    println(" |${('a'..('a' + fieldInfo.field[0].size - 1)).joinToString("")}|")
    println("-|----------|")
    println(fieldToPrint.withIndex().joinToString("\n") { (rowId, row) ->
        ('a' + rowId).toString() + row.joinToString(separator = "", prefix = "|", postfix = "|")
    })
    println("-|----------|")
}

fun openCell(fieldInfo: FieldInfo, point: Pair<Int, Int>): Boolean {
    if (point in fieldInfo.opened)
        return false

    if (fieldInfo.field[point.first][point.second] == MINE_CHAR)
        return true

    if (point in fieldInfo.marks)
        fieldInfo.marks -= point

    fieldInfo.opened += point
    if (fieldInfo.field[point.first][point.second] == EMPTY_CELL_CHAR)
        openNeighbours(fieldInfo, point)

    return false
}

fun openNeighbours(fieldInfo: FieldInfo, point: Pair<Int, Int>) {
    val deltas = intArrayOf(-1, 0, 1)

    for (dRowId in deltas)
        for (dColId in deltas) {
            if (point.first + dRowId !in fieldInfo.field.indices ||
                point.second + dColId !in fieldInfo.field[0].indices
            ) {
                continue
            }

            if (Pair(point.first + dRowId, point.second + dColId) !in fieldInfo.opened)
                openCell(fieldInfo, Pair(point.first + dRowId, point.second + dColId))
        }
}

fun checkGame(fieldInfo: FieldInfo): Boolean {
    if (fieldInfo.opened.size == fieldInfo.field.size * fieldInfo.field[0].size - fieldInfo.mineCount)
        return true

    if (fieldInfo.marks.size != fieldInfo.mineCount)
        return false

    for ((rowId, colId) in fieldInfo.marks) {
        if (fieldInfo.field[rowId][colId] != MINE_CHAR)
            return false
    }

    return true
}

fun mark(fieldInfo: FieldInfo, point: Pair<Int, Int>) {
    if (point !in fieldInfo.opened)
        fieldInfo.marks += point
}

const val FIELD_SIZE = 10
const val MAX_MINES_COUNT = FIELD_SIZE * FIELD_SIZE - 1

fun main() {
    val scanner = Scanner(System.`in`)
    var mineCount: Int

    do {
        print("How many mines do you want on the field(from 1 to $MAX_MINES_COUNT)? ")
        mineCount = scanner.nextInt()
    } while (mineCount !in 1..MAX_MINES_COUNT)

    val fieldInfo = FieldInfo(
        Array(FIELD_SIZE) { CharArray(FIELD_SIZE) },
        mineCount,
        mutableSetOf(),
        mutableSetOf()
    )

    printField(fieldInfo, false)
    var generationPoint: Pair<Int, Int>
    do {
        print("Choose first cell to free: ")
        val x = scanner.next().first()
        val y = scanner.next().first()
        generationPoint = Pair(y - 'a', x - 'a')
    } while (generationPoint.first !in fieldInfo.field.indices ||
        generationPoint.second !in fieldInfo.field[0].indices
    )

    generateMines(fieldInfo, generationPoint)
    countMines(fieldInfo)
    openCell(fieldInfo, generationPoint)
    printField(fieldInfo, false)

    var isUserFail = false
    while (!checkGame(fieldInfo) && !isUserFail) {
        print("Set/unset mines marks or claim a cell as free(coordinates and command): ")
        val x = scanner.next().first()
        val y = scanner.next().first()
        val cmd = scanner.next()

        val point = Pair(y - 'a', x - 'a')

        if (point.first in fieldInfo.field.indices && point.second in fieldInfo.field[0].indices) {
            isUserFail = when (cmd) {
                "free" -> openCell(fieldInfo, point)
                "mark" -> {
                    mark(fieldInfo, point)
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