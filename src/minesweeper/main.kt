import java.util.*

enum class CellState {
    EMPTY,
    OPENED,
    NUMBERED,
    MARKED,
    MINED
}

class Minesweeper(
    private val minesCount: Int,
    private var uncheckedCells: Int = (FIELD_SIZE - 2) * (FIELD_SIZE - 2)
) {
    companion object FieldProperties {
        private const val FIELD_SIZE = 11
    }

    private var field: MutableList<MutableList<Char>> = mutableListOf()
    private var visibleField: MutableList<MutableList<CellState>> =
        MutableList(FIELD_SIZE) { MutableList(FIELD_SIZE) { CellState.EMPTY } }

    private var created = false

    private fun createField(RowId: Int, ColId: Int) {
        val mines: MutableSet<Pair<Int, Int>> = mutableSetOf()
        val freeCells: MutableSet<Pair<Int, Int>> =
            (1..FIELD_SIZE - 2).flatMap { rowId ->
                (1..FIELD_SIZE - 2).map { colId -> rowId to colId }
            }.toMutableSet()
        freeCells.remove(Pair(RowId, ColId))

        while (mines.size != minesCount) {
            val cell = freeCells.random()
            mines.add(cell)
            freeCells.remove(cell)
        }

        field = (0 until FIELD_SIZE).map { rowId ->
            (0 until FIELD_SIZE).map { colId ->
                if (mines.contains(rowId to colId)) 'X' else '.'
            }.toMutableList()
        }.toMutableList()

        for (i in 1 until FIELD_SIZE - 1) {
            for (j in 1 until FIELD_SIZE - 1) {
                if (field[i][j] == 'X')
                    continue

                val cnt = countMinesAround(i, j)
                field[i][j] = if (cnt > 0) (cnt + '0'.toInt()).toChar() else '.'
            }
        }
    }

    private fun countMinesAround(RowId: Int, ColId: Int): Int {
        var res = 0
        for (dx in -1..1) {
            for (dy in -1..1) {
                res += if (field[RowId + dx][ColId + dy] == 'X') 1 else 0
            }
        }
        return res
    }

    fun printField() {
        println(" |abcdefghi|\n-|${"-".repeat(FIELD_SIZE - 2)}|")
        for (i in 1 until FIELD_SIZE - 1) {
            print("$i|")
            for (j in 1 until FIELD_SIZE - 1) {
                printCell(i, j)
            }
            println("|")
        }
    }

    private fun printCell(RowId: Int, ColId: Int) {
        print(getVisibleState(RowId, ColId))
    }

    private fun getVisibleState(RowId: Int, ColId: Int): Char = when (visibleField[RowId][ColId]) {
        CellState.MARKED -> '*'
        CellState.OPENED -> '#'
        CellState.EMPTY -> '.'
        CellState.NUMBERED -> field[RowId][ColId]
        CellState.MINED -> 'X'
    }

    fun checkForWin(): Boolean = uncheckedCells == minesCount

    fun processQuery(RowId: Int, ColId: Int, query: String): String {
        when (query) {
            "mine" -> return setMark(RowId, ColId)
            "free" -> return exploreCell(RowId, ColId)
            else -> return "Typo in query. Try again."
        }
    }

    private fun setMark(RowId: Int, ColId: Int): String {
        if (!this.created)
            return "No cells were opened"

        when (visibleField[RowId][ColId]) {
            CellState.MARKED -> visibleField[RowId][ColId] = CellState.EMPTY
            CellState.EMPTY -> visibleField[RowId][ColId] = CellState.MARKED
            CellState.OPENED -> return "Cell is opened"
            CellState.NUMBERED -> return "Cell is opened"
            else -> {
            }
        }

        return ""
    }

    private fun exploreCell(RowId: Int, ColId: Int): String {
        if (RowId !in 1..FIELD_SIZE - 2 || ColId !in 1..FIELD_SIZE - 2)
            return ""

        if (!this.created) {
            createField(RowId, ColId)
            this.created = true
        }

        if (field[RowId][ColId] == 'X') {
            onLosing()
            return "End"
        }

        --uncheckedCells

        visibleField[RowId][ColId] = if (field[RowId][ColId] == '.') CellState.OPENED else CellState.NUMBERED
        if (visibleField[RowId][ColId] == CellState.OPENED) {
            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (visibleField[RowId + dx][ColId + dy] == CellState.EMPTY)
                        exploreCell(RowId + dx, ColId + dy)
                }
            }
        }

        return ""
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
    print("Type in the number of mines you'd like to see: ")
    val n = readLine()!!.toInt()
    val minesweeper = Minesweeper(n)

    val scanner = Scanner(System.`in`)
    while (!minesweeper.checkForWin()) {
        minesweeper.printField()

        val x = scanner.nextInt()
        val y = scanner.next().toString()[0] - 'a' + 1
        val query = scanner.next()

        val response = minesweeper.processQuery(x, y, query)
        if (response == "End")
            break
        else if (!response.isEmpty())
            println(response)

    }

    minesweeper.printField()

    println(
        if (minesweeper.checkForWin()) "Congratulations! You've found all mines!"
        else "You stepped on a mine and failed!"
    )
}
