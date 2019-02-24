package minesweeper

import java.util.*

const val FIELD_SIZE = 10
const val MAX_MINES_COUNT = FIELD_SIZE * FIELD_SIZE - 1
val WHITESPACES = "[\\t ]+".toRegex()

fun main() {
    val scanner = Scanner(System.`in`)
    var mineCount: Int

    do {
        print("How many mines do you want on the field(from 1 to $MAX_MINES_COUNT)? ")
        mineCount = scanner.nextInt()
    } while (mineCount !in 1..MAX_MINES_COUNT)
    scanner.nextLine() // так как сканнер считал всю линию, а взял из неё только инт

    val game = Game(FieldSize(FIELD_SIZE, FIELD_SIZE), mineCount)

    game.printField(false)
    var generationPoint: CellPosition
    do {
        val (cell, _) = scanner.readUserCommand("Choose first cell to free(e.g. 7 c): ") ?: return
        generationPoint = cell
    } while (generationPoint.rowId !in game.rowIndices ||
        generationPoint.colId !in game.colIndices
    )

    game.initializeGame(generationPoint)
    game.printField(false)

    var isUserFail = false
    while (!game.checkGameToWin && !isUserFail) {
        val (cell, cmd) = scanner.readUserCommand(
            "Set/unset mines marks or claim a cell as free(coordinates(e.g. 2 a) and command(mark or free)): "
        ) ?: return

        if (cell.rowId in game.rowIndices && cell.rowId in game.rowIndices) {
            isUserFail = when (cmd) {
                Command.FREE.toString() -> game.openCell(cell)
                Command.MARK.toString() -> {
                    game.markCell(cell)
                    false
                }
                else -> false
            }
        }
        game.printField(isUserFail)
    }

    if (!isUserFail)
        println("Congratulations! You found all mines!")
    else
        println("You stepped on a mine and failed!")
}