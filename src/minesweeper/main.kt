package minesweeper

import java.util.*

const val fieldCeilCount = 81;

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)

    print("How many mines do you want on the field? ")

    var count = scanner.nextInt()

    if (count > fieldCeilCount) {
        print("Invalid mines count!")

        return
    }

    val random = Random()
    val field = BooleanArray(fieldCeilCount)

    while (count > 0) {
        val index = random.nextInt(fieldCeilCount - 1)

        if (!field[index]) {
            field[index] = true
            count--
        }
    }

    for (i in 0..(fieldCeilCount - 1)) {
        if (i != 0 && i % 9 == 0) {
            println()
        }

        if (field[i]) {
            print('X')
        } else {
            print('.')
        }
    }
}