package com.company

import java.util.Scanner
import java.util.Random

const val SIZE = 9
const val SPACE = '.'
const val MINE = 'X'
const val MARK = '*'
const val OPEN = '/'
var random = Random()
val scanner = Scanner(System.`in`)

fun generateMap(map: Array<Array<Char>>, n: Int) {
    var x: Int
    var y: Int
    for (i in 1..SIZE)
        for (j in 1..SIZE)
            map[i][j] = SPACE
    for (i in 1..n) {
        do {
            x = random.nextInt(SIZE) + 1
            y = random.nextInt(SIZE) + 1
        } while (map[x][y] == MINE)
        map[x][y] = MINE
    }

}

fun calculateMap(map: Array<Array<Char>>) {
    val indexes = arrayOf(-1, 0, 1)
    for (i in 1..SIZE)
        for (j in 1..SIZE)
            if (map[i][j] == SPACE) {
                var sum = 0
                for (k in indexes)
                    for (p in indexes)
                        if (map[i + k][p + j] == MINE)
                            sum++
                if (sum != 0)
                    map[i][j] = sum.toString().first()
            }
}

fun Array<Array<Char>>.printMap() {
    println()
    println(" |123456789|")
    println("-|---------|")
    for (i in 1..SIZE)
        println(this[i].sliceArray(1..SIZE).joinToString(separator = "",prefix = "$i|", postfix = "|"))
    println("-|---------|")
}
fun Array<Array<Char>>.getMarks (a: Char): Set<Pair<Int,Int>> {
    var mines: MutableList<Pair<Int,Int>> = arrayListOf()
    for (i in 1..SIZE)
        for (j in 1..SIZE)
        if (this[i][j] == a){
            mines.add(Pair(i,j))
        }
    return mines.toSet()
}
fun Array<Array<Char>>.setMark(mine: Pair<Int, Int>, c: Char){
    this[mine.first][mine.second]=c
}
fun Array<Array<Char>>.getCell(cell : Pair<Int, Int>) = this[cell.first][cell.second]
fun Array<Array<Char>>.explore(cell : Pair<Int, Int>, mapPrint: Array<Array<Char>>){
    val indexes = arrayOf(-1, 0, 1)
    when (this.getCell(cell)){
        SPACE ->
        {
            this.setMark(cell, OPEN)
            mapPrint.setMark(cell, OPEN)
            for (k in indexes)
                for (p in indexes)
                    explore(Pair(cell.first+k, cell.second+p), mapPrint)
        }
        in '1'..'9' ->{
            mapPrint.setMark(cell, this.getCell(cell))
        }

    }
}

fun Array<Array<Char>>.allCellsOpen(mines: Set<Pair<Int, Int>>): Boolean{
    var set = this.getMarks(SPACE).toMutableSet()
    set.addAll(this.getMarks(MARK))
    return mines==set
}

fun play(map: Array<Array<Char>>, mines: Set<Pair<Int,Int>>){
    val mapPrint: Array<Array<Char>> = Array(SIZE + 2, { Array(SIZE + 2, { SPACE }) })
    mapPrint.printMap()
    var marks: MutableSet<Pair<Int,Int>> = mutableSetOf()
    while (!marks.equals(mines) && !mapPrint.allCellsOpen(mines)){
        var cell: Pair<Int, Int>? = null
        do{
            print("Set/unset mines marks (x and y coordinates): ")
            val n1 = scanner.nextInt()
            val n2 = scanner.nextInt()
            val str = scanner.next()
            if ( n1 in 1..SIZE && n2 in 1..SIZE ){
                cell = Pair(n2,n1)
                when (str){
                    "free" -> {
                        when (map.getCell(cell)) {
                            SPACE -> map.explore(cell, mapPrint)
                            in '1'..'9' ->{
                                mapPrint.setMark(cell, map.getCell(cell))
                            }
                            MINE -> {
                                println()
                                println("You stepped on a mine and failed!")
                                return
                            }
                        }                                                
                    }
                    "mine" -> {
                        when (mapPrint.getCell(cell)){
                            SPACE -> {
                                marks.add(cell)
                                mapPrint.setMark(cell,MARK)
                            }
                            MARK ->{
                                marks.remove(cell)
                                mapPrint.setMark(cell, SPACE)
                            }
                            else -> cell = null
                        }
                    }
                    else -> cell = null
                }
            }
        } while ( cell == null)
        mapPrint.printMap()
    }
    println()
    println("Congratulations! You founded all mines!")
}

fun main(args: Array<String>) {
    val map: Array<Array<Char>> = Array(SIZE + 2, { Array(SIZE + 2, { OPEN }) })
    var n: Int
    do {
        print("How many mines do you want on the field? ")
        n = scanner.nextInt()
    } while (n < 0 || n > SIZE * SIZE)
    generateMap(map, n)
    calculateMap(map)
    val mines = map.getMarks(MINE)
    play(map,mines)
}
