import java.util.*

enum class CellState(private var visibleChar: Char?) {
    EMPTY('.'),
    OPENED('#'),
    NUMBERED(null),
    MARKED('*'),
    MINED('X');

    fun getVisibleState(minesCount: Char): Char = visibleChar ?: minesCount
}

class Minesweeper(
    private val minesCount: Int,
    private var uncheckedCells: Int = (FIELD_SIZE - 2) * (FIELD_SIZE - 2)
) {
    companion object {
        private const val FIELD_SIZE = 11
        const val GAME_ENDED = "End"
    }

    private var field: MutableList<MutableList<Char>> = MutableList(FIELD_SIZE) { MutableList(FIELD_SIZE) { '.' } }
    private val visibleField: MutableList<MutableList<CellState>> =
        MutableList(FIELD_SIZE) { MutableList(FIELD_SIZE) { CellState.EMPTY } }

    private var created = false

    private fun createField(rowId: Int, colId: Int) {
        val mines: MutableSet<Pair<Int, Int>> = mutableSetOf()
        val freeCells: MutableSet<Pair<Int, Int>> =
            (1..FIELD_SIZE - 2).flatMap { row ->
                (1..FIELD_SIZE - 2).map { col -> row to col }
            }.toMutableSet()
        freeCells.remove(Pair(rowId, colId))

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

    private fun countMinesAround(rowId: Int, colId: Int): Int {
        var res = 0
        for (dx in -1..1) {
            for (dy in -1..1) {
                res += if (field[rowId + dx][colId + dy] == 'X') 1 else 0
            }
        }
        return res
    }

    fun getField(): String {
        var fieldString = ""
        fieldString += " |${(1..FIELD_SIZE - 2).map { 'a' + it - 1 }.joinToString("")}|\n-|${"-".repeat(FIELD_SIZE - 2)}|\n"
        for (i in 1 until FIELD_SIZE - 1) {

            fieldString += "$i|"
            for (j in 1 until FIELD_SIZE - 1) {
                fieldString += visibleField[i][j].getVisibleState(minesCount = field[i][j])
            }
            fieldString += "|\n"
        }

        return fieldString
    }

    fun checkForWin(): Boolean = uncheckedCells == minesCount

    fun processQuery(rowId: Int, colId: Int, query: String): String = when (query) {
        "mine" -> setMark(rowId, colId)
        "free" -> exploreCell(rowId, colId)
        else -> "Typo in query. Try again."
    }

    private fun setMark(rowId: Int, colId: Int): String {
        if (!this.created)
            return "No cells were opened"

        when (visibleField[rowId][colId]) {
            CellState.MARKED -> visibleField[rowId][colId] = CellState.EMPTY
            CellState.EMPTY -> visibleField[rowId][colId] = CellState.MARKED
            CellState.OPENED -> return "Cell is opened"
            CellState.NUMBERED -> return "Cell is opened"
            else -> {
            }
        }

        return ""
    }

    private fun exploreCell(rowId: Int, colId: Int): String {
        if (rowId !in 1..FIELD_SIZE - 2 || colId !in 1..FIELD_SIZE - 2)
            return "Wrong coordinates."

        if (!this.created) {
            createField(rowId, colId)
            this.created = true
        }

        if (field[rowId][colId] == 'X') {
            onLosing()
            return "End"
        }

        --uncheckedCells

        visibleField[rowId][colId] = if (field[rowId][colId] == '.') CellState.OPENED else CellState.NUMBERED
        if (visibleField[rowId][colId] == CellState.OPENED) {
            for (dx in -1..1) {
                for (dy in -1..1) {
                    if (visibleField[rowId + dx][colId + dy] == CellState.EMPTY)
                        exploreCell(rowId + dx, colId + dy)
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

fun main() {
    print("Type in the number of mines you'd like to see: ")
    val n = readLine()!!.toInt()
    val minesweeper = Minesweeper(n)

    val scanner = Scanner(System.`in`)
    while (!minesweeper.checkForWin()) {
        print(minesweeper.getField())

        print("Enter coordinates and command (free/mine) i.e. \"1 a free\" (without quotes): ")
        val x = scanner.nextInt()
        val y = scanner.next().toString()[0] - 'a' + 1
        val query = scanner.next()

        val response = minesweeper.processQuery(x, y, query)
        if (response == Minesweeper.GAME_ENDED)
            break
        else if (!response.isEmpty())
            println(response)

    }

    print(minesweeper.getField())

    println(
        if (minesweeper.checkForWin()) "Congratulations! You've found all mines!"
        else "You stepped on a mine and failed!"
    )
}
