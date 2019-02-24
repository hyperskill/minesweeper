package minesweeper

import java.util.*

enum class Command(private val stringValue: String) {
    FREE("free"),
    MARK("mark");

    override fun toString() = stringValue
}

data class UserInput(val cell: CellPosition, val cmd: String?)

fun Scanner.readUserCommand(msg: String): UserInput? {
    var cmd: String? = null
    var rowId: Int? = null
    var colId: Int? = null

    do {
        print(msg)

        val enteredValues = WHITESPACES.split(nextLine() ?: return null)

        if (enteredValues.size < 2 || enteredValues[0].isEmpty() || enteredValues[1].isEmpty())
            continue

        rowId = enteredValues[0].toIntOrNull() ?: enteredValues[1].toIntOrNull()

        colId = when {
            enteredValues[1].first() in 'a'..'z' -> enteredValues[1].first() - 'a'
            enteredValues[0].first() in 'a'..'z' -> enteredValues[0].first() - 'a'
            else -> null
        }

        if (enteredValues.size > 2)
            cmd = enteredValues[2]

    } while (rowId == null || colId == null)

    return UserInput(CellPosition(rowId - 1, colId), cmd)
}