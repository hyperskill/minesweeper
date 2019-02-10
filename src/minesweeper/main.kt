package minesweeper

import java.util.*
import kotlin.random.Random

class FieldInfo(val field: Array<CharArray>,
                val size: Pair<Int, Int>,
                val mineCount: Int,
                val marks: MutableSet<Pair<Int, Int>>,
                val opened: MutableSet<Pair<Int, Int>>)

fun generateMines(fieldInfo: FieldInfo, generationPoint: Pair<Int, Int>) {
    var needMine = fieldInfo.mineCount
    for (i in 0 until fieldInfo.size.first) {
        for (j in 0 until fieldInfo.size.second) {
            if (i == generationPoint.first && j == generationPoint.second) {
                if (needMine < fieldInfo.size.first * fieldInfo.size.second - i * fieldInfo.size.second - j)
                    continue
                else {
                    val firstNotMine = findFirstNoMine(fieldInfo)
                    fieldInfo.field[firstNotMine.first][firstNotMine.second] = 'X'
                    needMine--
                    continue
                }
            }

            if (needMine > 0
                    && (Random.nextInt(fieldInfo.size.first * fieldInfo.size.second - 1) <= fieldInfo.mineCount
                    || needMine == fieldInfo.size.first * fieldInfo.size.second - i * fieldInfo.size.second - j))
            {
                needMine--
                fieldInfo.field[i][j] = 'X'
            } else
                fieldInfo.field[i][j] = '.'
        }
    }
}

fun findFirstNoMine(fieldInfo: FieldInfo): Pair<Int, Int> {
    for (i in 0 until fieldInfo.size.first)
        for (j in 0 until fieldInfo.size.second)
            if (fieldInfo.field[i][j] != 'X')
                return Pair(i, j)

    return Pair(-1, -1)
}

fun countMines(fieldInfo: FieldInfo) {
    for (i in 0 until fieldInfo.size.first)
        for (j in 0 until fieldInfo.size.second)
            if (fieldInfo.field[i][j] != 'X') {
                val mineCount = countMinesAroundCell(fieldInfo, i, j)
                if (mineCount != 0)
                    fieldInfo.field[i][j] = '0' + mineCount
                else
                    fieldInfo.field[i][j] = '\\'
            }
}

fun countMinesAroundCell(fieldInfo: FieldInfo, i: Int, j: Int): Int {
    val delta = intArrayOf(-1, 0, 1)

    var cnt = 0
    for (dx in delta)
        for (dy in delta)
            if (!(dx == 0 && dy == 0)
                    && i + dx in 0 until fieldInfo.size.first
                    && j + dy in 0 until fieldInfo.size.second
                    && fieldInfo.field[dx + i][dy + j] == 'X')
                cnt++

    return cnt
}

fun printField(fieldInfo: FieldInfo, show: Boolean) {

    val fieldToPrint = fieldInfo.field.map { it.copyOf() }.toTypedArray()

    if (!show) {
        for (i in 0 until fieldInfo.size.first)
            for (j in 0 until fieldInfo.size.second)
                if (Pair(i, j) !in fieldInfo.opened)
                    fieldToPrint[i][j] = '.'

        for ((i, j) in fieldInfo.marks) {
            fieldToPrint[i][j] = '*'
        }
    }

    println(" |${('a'..('a' + fieldInfo.size.second - 1)).joinToString("")}|")
    println("-|----------|")
    var ind = 0
    println(fieldToPrint.joinToString("\n") {
        ('a' + ind++).toString() + it.joinToString(separator = "", prefix = "|", postfix = "|")
    })
    println("-|----------|")
}

fun openCell(fieldInfo: FieldInfo, point: Pair<Int, Int>): Boolean {
    if (point in fieldInfo.opened)
        return false

    if (fieldInfo.field[point.first][point.second] == 'X')
        return true

    if (point in fieldInfo.marks)
        fieldInfo.marks -= point

    fieldInfo.opened += point
    if (fieldInfo.field[point.first][point.second] == '\\')
        openNeighbours(fieldInfo, point)

    return false
}

fun openNeighbours(fieldInfo: FieldInfo, point: Pair<Int, Int>) {
    val delta = intArrayOf(-1, 0, 1)

    for (dx in delta)
        for (dy in delta) {
            if (point.first + dx !in 0 until fieldInfo.size.first
                    || point.second + dy !in 0 until fieldInfo.size.second)
                continue

            if (Pair(point.first + dx, point.second + dy) !in fieldInfo.opened)
                openCell(fieldInfo, Pair(point.first + dx, point.second + dy))
        }
}

fun checkGame(fieldInfo: FieldInfo): Boolean {
    if (fieldInfo.opened.size == fieldInfo.size.first * fieldInfo.size.second - fieldInfo.mineCount)
        return true

    if (fieldInfo.marks.size != fieldInfo.mineCount)
        return false

    for ((i, j) in fieldInfo.marks) {
        if (fieldInfo.field[i][j] != 'X')
            return false
    }

    return true
}

fun mark(fieldInfo: FieldInfo, point: Pair<Int, Int>) {
    if (point !in fieldInfo.opened)
        fieldInfo.marks += point
}

fun main() {
    val scanner = Scanner(System.`in`)
    var mineCount: Int

    do {
        print("How many mines do you want on the field? ")
        mineCount = scanner.nextInt()
    } while (mineCount !in 1..99)

    val fieldInfo = FieldInfo(
            Array(10) { CharArray(10) },
            Pair(10, 10),
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
    } while (generationPoint.first !in 0 until fieldInfo.size.first
            || generationPoint.second !in 0 until fieldInfo.size.second)

    generateMines(fieldInfo, generationPoint)
    countMines(fieldInfo)
    openCell(fieldInfo, generationPoint)
    printField(fieldInfo, false)

    var isFail = false
    while (!checkGame(fieldInfo) && !isFail) {
        print("Set/unset mines marks or claim a cell as free: ")
        val x = scanner.next().first()
        val y = scanner.next().first()
        val cmd = scanner.next()

        val point = Pair(y - 'a', x - 'a')

        if (point.first in 0 until fieldInfo.size.first && point.second in 0 until fieldInfo.size.second) {
            isFail = when (cmd) {
                "free" -> openCell(fieldInfo, point)
                "mark" -> {
                    mark(fieldInfo, point)
                    false
                }
                else -> false
            }
        }
        printField(fieldInfo, isFail)
    }

    if (!isFail)
        println("Congratulations! You founded all mines!")
    else
        println("You stepped on a mine and failed!")
}