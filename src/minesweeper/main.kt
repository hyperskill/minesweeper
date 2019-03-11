package minesweeper

import java.util.*

const val FIELD_SIZE = 10
const val MAX_MINES_COUNT = FIELD_SIZE * FIELD_SIZE - 1

fun main() {
    val scanner = Scanner(System.`in`)
    var mineCount: Int

    do {
        print("How many mines do you want on the field(from 1 to $MAX_MINES_COUNT)? ")
        mineCount = scanner.nextInt()
    } while (mineCount !in 1..MAX_MINES_COUNT)
    scanner.nextLine() // так как сканнер считал всю линию, а взял из неё только инт

    val game = Game(FieldSize(FIELD_SIZE, FIELD_SIZE), mineCount)

    println(game.toString(false))
    var generationPoint: CellPosition
    do {
        val (cell, _) = scanner.readUserCommand("Choose first cell to free(e.g. 7 c): ") ?: return
        generationPoint = cell
    } while (!game.isValidCellPosition(generationPoint))

    game.openCell(generationPoint)
    println(game.toString(false))

    var isUserFail = false
    while (!game.checkGameToWin && !isUserFail) {
        val (cell, cmd) = scanner.readUserCommand(
            "Set/unset mines marks or claim a cell as free(coordinates(e.g. 2 a) and command(mark or free)): "
        ) ?: return

        if (game.isValidCellPosition(cell)) {
            isUserFail = when (cmd) {
                Command.FREE -> game.openCell(cell)
                Command.MARK -> game.markCell(cell)
                else -> false
            }
        }
        println(game.toString(isUserFail))
    }

    if (!isUserFail)
        println("Congratulations! You found all mines!")
    else
        println("You stepped on a mine and failed!")
}