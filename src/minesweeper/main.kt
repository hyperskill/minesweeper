package minesweeper

import java.util.*

const val rows = 9
const val cols = 9
const val mine = 'X'
const val mark = '*'
const val markedMine = '+'
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
                mine -> print(empty)
                null -> print(cellInfo(field, row, col))
            }
        }

        println('│')
    }

    printSeparate(cols)
}

fun markCell(field: Array<Array<Char?>>, rowIndex:Int, colIndex:Int) {
    field[rowIndex][colIndex] = when(field[rowIndex][colIndex]) {
        mine -> markedMine
        markedMine -> mine
        else -> mark
    }
}

fun isWin(field: Array<Array<Char?>>): Boolean {
    for (row in 0..field.lastIndex) {
        for (col in 0..field[row].lastIndex) {
            if (field[row][col] == mine) {
                return false
            }
        }
    }

    return true
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    print("How many mines do you want on the field? ")

    val minesCount = scanner.nextInt()
    val field = generateField(minesCount)

    while (true) {
        printField(field)
        print("Set/delete mines marks (x and y coordinates): ")

        val x = scanner.next().toUpperCase()
        val y = scanner.nextInt()

        markCell(field, y - 1, x[0].toInt() - 'A'.toInt())

        if (isWin(field)) {
            break;
        }

        println()
    }

    println("Congratulations! You founded all mines!")
}