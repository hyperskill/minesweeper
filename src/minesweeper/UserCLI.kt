package minesweeper

import java.util.*

enum class Command(private val stringValue: String) {
    FREE("free"),
    MARK("mark");

    override fun toString() = stringValue

    companion object {
        fun fromString(command: String?): Command? = when (command) {
            FREE.stringValue -> FREE
            MARK.stringValue -> MARK
            else -> null
        }
    }
}

data class UserInput(val cell: CellPosition, val cmd: Command?)

private fun readCoordinatesFromTokens(tokens: List<String>): CellPosition? {
    if (tokens.size < 2 || tokens[0].isEmpty() || tokens[1].isEmpty())
        return null

    val rowId = tokens[0].toIntOrNull() ?: tokens[1].toIntOrNull()

    val colId = when {
        tokens[1].first() in 'a'..'z' -> tokens[1].first() - 'a'
        tokens[0].first() in 'a'..'z' -> tokens[0].first() - 'a'
        else -> null
    }

    return if (rowId != null && colId != null) CellPosition(rowId, colId) else null
}

private val WHITESPACES = "[\\t ]+".toRegex()

fun Scanner.readUserCommand(msg: String): UserInput? {
    var cmd: Command?
    var cellPosition: CellPosition?

    do {
        print(msg)

        val enteredValues = WHITESPACES.split(nextLine() ?: return null)

        cellPosition = readCoordinatesFromTokens(enteredValues)

        cmd = Command.fromString(enteredValues.getOrNull(2))
    } while (cellPosition == null)

    return UserInput(cellPosition, cmd)
}