import java.util.*
import kotlin.random.Random

enum class CellState {
    EMPTY,
    OPENED,
    NUMBERED,
    MARKED,
    MINED,
}

class Minesweeper(cntOfMines: Int) {
    private val FIELD_SIZE = 11

    private var field: MutableList<MutableList<Char>> = mutableListOf()
    private var visibleField: MutableList<MutableList<CellState>> =
            MutableList(FIELD_SIZE) { MutableList(FIELD_SIZE) { CellState.EMPTY } }

    private var markedMines = 0
    private var cntOfMarks = 0
    private var cntOfMines = 0
    private var uncheckedCells = (FIELD_SIZE - 2) * (FIELD_SIZE - 2)

    init {
        this.cntOfMines = cntOfMines
        var mines: MutableSet<Pair<Int, Int>> = mutableSetOf()
        var freeCells: MutableList<Pair<Int, Int>> =
                MutableList((FIELD_SIZE - 2) * (FIELD_SIZE - 2))
                { index -> index / (FIELD_SIZE - 2) + 1 to index % (FIELD_SIZE - 2) + 1 }

        while (mines.size != cntOfMines) {
            val id = Random.nextInt(0, freeCells.size)
            mines.add(freeCells[id])
            freeCells.removeAt(id)
        }

        for (i in 0 until FIELD_SIZE) {
            field.plusAssign(mutableListOf<Char>())

            for (j in 0 until FIELD_SIZE) {
                if (mines.contains(i to j)) {
                    field[i].plusAssign('X')
                } else {
                    field[i].plusAssign('.')
                }
            }
        }

        for (i in 1 until FIELD_SIZE - 1) {
            for (j in 1 until FIELD_SIZE - 1) {
                if (field[i][j] == 'X')
                    continue

                val cnt = countMinesAround(i, j)
                field[i][j] = if (cnt > 0) (cnt + '0'.toInt()).toChar() else '.'
            }
        }
    }

    private fun countMinesAround(x: Int, y: Int): Int {
        var res = 0
        for (dx in -1..1) {
            for (dy in -1..1) {
                res += if (field[x + dx][y + dy] == 'X') 1 else 0
            }
        }
        return res
    }

    fun printField() {
        println(" |123456789|\n-|${"-".repeat(9)}|")
        for (i in 1 until FIELD_SIZE - 1) {
            print("$i|")
            for (j in 1 until FIELD_SIZE - 1) {
                printCell(i, j)
            }
            println("|")
        }
    }

    private fun printCell(x: Int, y: Int) {
        val state = visibleField[x][y]
        print(when (state) {
            CellState.MARKED -> "*"
            CellState.OPENED -> "#"
            CellState.EMPTY -> "."
            CellState.NUMBERED -> field[x][y]
            CellState.MINED -> 'X'
        })
    }

    fun checkForWin(): Boolean = markedMines == cntOfMines && markedMines == cntOfMarks ||
                                uncheckedCells == cntOfMines

    fun processQuery(x: Int, y: Int, query: String): Boolean {
        when (query) {
            "mine" -> setMark(x, y)
            "free" -> return exploreCell(x, y)
            else -> print("Typo in query. Try again.\n")
        }

        return true
    }

    private fun setMark(x: Int, y: Int) {
        when (visibleField[x][y]) {
            CellState.MARKED -> {
                visibleField[x][y] = CellState.EMPTY
                --cntOfMarks
                if (field[x][y] == 'X')
                    --markedMines
            }

            CellState.EMPTY -> {
                visibleField[x][y] = CellState.MARKED
                ++cntOfMarks
                if (field[x][y] == 'X')
                    ++markedMines
            }

            CellState.OPENED -> print("Cell is opened\n")
            CellState.NUMBERED -> print("Cell is opened\n")
            else -> {}
        }
    }

    private fun exploreCell(x: Int, y: Int): Boolean {
        if (x < 1 || x > FIELD_SIZE - 2 || y < 1 || y > FIELD_SIZE - 2)
            return true

        if (field[x][y] == 'X') {
            onLosing()
            return false
        }

        --uncheckedCells

        visibleField[x][y] = if (field[x][y] == '.') CellState.OPENED else CellState.NUMBERED
        if (visibleField[x][y] == CellState.OPENED) {
            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (visibleField[x + dx][y + dy] == CellState.EMPTY)
                        exploreCell(x + dx, y + dy)
                }
            }
        }

        return true
    }

    private fun onLosing() {
        for (i in 1 until FIELD_SIZE - 1) {
            for (j in 1 until FIELD_SIZE - 1) {
                if (field[i][j] == 'X')
                    visibleField[i][j] = CellState.MINED
            }
        }
    }
}

fun main(args: Array<String>) {
    print("How many mines do you want on the field? ")
    val n = readLine()!!.toInt()
    val minesweeper = Minesweeper(n)

    while (!minesweeper.checkForWin()) {
        minesweeper.printField()

        val scanner = Scanner(System.`in`)
        val x = scanner.nextInt()
        val y = scanner.nextInt()
        val query = scanner.next()

        if (!minesweeper.processQuery(x, y, query))
            break
    }

    minesweeper.printField()

    println(if (minesweeper.checkForWin()) "Congratulations! You founded all mines!"
            else "You stepped on a mine and failed!")
}
