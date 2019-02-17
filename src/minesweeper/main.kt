package minesweeper

import java.util.*
const val rows = 9
const val columns = 9
const val fieldCeilCount = rows * columns

fun generateField(minesCount: Int): CharArray {
    val random = Random()
    val field = CharArray(fieldCeilCount)
    var count = minesCount

    while (count > 0) {
        val index = random.nextInt(fieldCeilCount - 1)

        if (field[index] != 'X') {
            field[index] = 'X'
            count--
        }
    }

    return field
}

fun isMineExist(fieldIndex: Int, field: CharArray): Boolean {
    return fieldIndex >= 0 && fieldIndex < field.size && field[fieldIndex] == 'X'
}


fun calculateMinesInArea(fieldIndex: Int, field:CharArray): Int {
    var count = 0
    val topIndex = fieldIndex - columns
    val topRightIndex = topIndex + 1
    val topLeftIndex = topIndex - 1
    val rightIndex = fieldIndex + 1
    val leftIndex = fieldIndex - 1
    val bottomIndex = fieldIndex + columns
    val bottomRightIndex = bottomIndex + 1
    val bottomLeftIndex = bottomIndex - 1
    val isInOnLeftSide = fieldIndex % columns == 0
    val isInOnRightSide = fieldIndex % columns == columns - 1

    if(isMineExist(topIndex, field)) {
        count++
    }

    if(!isInOnRightSide && isMineExist(topRightIndex, field)) {
        count++
    }

    if(!isInOnLeftSide && isMineExist(topLeftIndex, field)) {
        count++
    }

    if(!isInOnRightSide && isMineExist(rightIndex, field)) {
        count++
    }

    if(!isInOnLeftSide && isMineExist(leftIndex, field)) {
        count++
    }

    if(isMineExist(bottomIndex, field)) {
        count++
    }

    if(!isInOnRightSide && isMineExist(bottomRightIndex, field)) {
        count++
    }

    if(!isInOnLeftSide && isMineExist(bottomLeftIndex, field)) {
        count++
    }

    return count
}

fun printField(field: CharArray) {
    for (i in 0..(fieldCeilCount - 1)) {
        if (i != 0 && i % columns == 0) {
            println()
        }

        if (field[i] == 'X') {
            print('X')
        } else {
            val minesCount = calculateMinesInArea(i, field)

            if (minesCount == 0) {
                print('.')
            } else {
                print(minesCount)
            }
        }
    }
}

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    print("How many mines do you want on the field? ")

    val minesCount = scanner.nextInt()
    val field = generateField(minesCount)

    printField(field)
}