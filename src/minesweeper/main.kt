package minesweeper

import java.util.*

const val rows = 9
const val cols = 9
const val mine = 'X'
const val mark = '*'
const val markedMine = '+'
const val discovered = '/'
const val empty = '.'

fun generateField(minesCount:Int): Array<Array<Char?>> {
    val field = Array(rows) { arrayOfNulls<Char>(cols) }
    val random = Random()
    var count = minesCount

    while (count > 0) {
        val randomRow = random.nextInt(rows)
        val randomCol = random.nextInt(rows)

        if (field[randomRow][randomCol] == null) {
            field[randomRow][randomCol] = mine
            count--
        }
    }

    return field
}

fun cellInfo(field: Array<Array<Char?>>, row:Int, col:Int): Char {
    var nearMinesCount = '0'

    for (r in (row - 1)..(row + 1)) {
        for (c in (col - 1)..(col + 1)) {
            if (r >= 0
                    && r <= field.lastIndex
                    && c >= 0
                    && c <= field[r].lastIndex
                    && (field[r][c] == mine || field[r][c] == markedMine)
            ) {
                nearMinesCount++
            }
        }
    }

    if (nearMinesCount != '0') {
        return nearMinesCount
    }

    return empty
}

fun printFieldHeader(cols: Int) {
    print(" │")

    for (i in 0..(cols - 1)) {
        print('A' + i)
    }

    println('│')
}

fun printSeparate(cols:Int) {
    print("—│")

    for (i in 1..cols) {
        print('—')
    }

    println('│')
}

fun printField(field:Array<Array<Char?>>) {
    printFieldHeader(cols)
    printSeparate(cols)

    for (row in 0..field.lastIndex) {
        print(row + 1)
        print('│')

        for (col in 0..field[row].lastIndex) {
            when(field[row][col]) {
                mark, markedMine -> print(mark)
                mine, null -> print(empty)
                else -> print(field[row][col])
            }
        }

        println('│')
    }

    printSeparate(cols)
}

fun freeCell(field: Array<Array<Char?>>, row: Int, col: Int) {
    if (field[row][col] != null) {
        return
    }

    val info = cellInfo(field, row, col)

    if (info != empty) {
        field[row][col] = info

        return
    }

    field[row][col] = discovered

    val rowRange = 0 until rows
    val colRange = 0 until cols

    if((row + 1) in rowRange) {
        freeCell(field, row + 1, col)
    }

    if ((row + 1) in rowRange && (col + 1) in colRange) {
        freeCell(field, row + 1, col + 1)
    }

    if ((col + 1) in 0 until cols) {
        freeCell(field, row, col + 1)
    }

    if ((row - 1) in rowRange && (col + 1) in colRange) {
        freeCell(field, row - 1, col + 1)
    }

    if ((row - 1) in rowRange) {
        freeCell(field, row - 1, col)
    }

    if ((row - 1) in rowRange && (col - 1) in colRange) {
        freeCell(field, row - 1, col - 1)
    }

    if ((col - 1) in colRange) {
        freeCell(field, row, col - 1)
    }

    if ((row + 1) in rowRange && (col - 1) in colRange) {
        freeCell(field, row + 1, col - 1)
    }
}

fun markCell(field: Array<Array<Char?>>, row:Int, col:Int) {
    field[row][col] = when(field[row][col]) {
        mine -> markedMine
        markedMine -> mine
        mark -> null
        else -> mark
    }
}

fun isWin(field: Array<Array<Char?>>): Boolean {
    var minesCount = 0
    var markCount = 0
    var emptyCellCount = 0

    for (row in 0..field.lastIndex) {
        for (col in 0..field[row].lastIndex) {
            when(field[row][col]) {
                mine -> minesCount++
                mark -> markCount++
                null -> emptyCellCount++
            }
        }
    }

    if (markCount > 0) {
        return false
    }

    return minesCount == 0 || emptyCellCount == 0
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    print("How many mines do you want on the field? ")

    val minesCount = scanner.nextInt()
    val field = generateField(minesCount)

    while (true) {
        printField(field)
        print("Set/delete mines marks (x and y coordinates): ")

        val col = scanner.next().toUpperCase()[0].toInt() - 'A'.toInt()
        val row = scanner.nextInt() - 1
        val action = scanner.next().toLowerCase()

        when(action) {
            "free" -> {
                if (field[row][col] == mine) {
                    println("Game over!")

                    return
                }

                freeCell(field, row, col)
            }
            "mine" -> markCell(field, row, col)
            else -> println("Unknown action")
        }

        if (isWin(field)) {
            break;
        }

        println()
    }

    println("Congratulations! You founded all mines!")
}