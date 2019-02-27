package com.company

import java.util.Scanner
import java.util.Random

const val SIZE = 9
const val SPACE = '.'
const val MINE = 'X'
const val MARK = '*'
var random = Random()

fun generateMap(map: Array<Array<Char>>, n: Int) {
    var x: Int
    var y: Int
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
fun Array<Array<Char>>.getMines(a: Char, b: Char): Set<Pair<Int,Int>> {
    var mines: MutableList<Pair<Int,Int>> = arrayListOf()
    for (i in 0 until  this.count())
        for (j in 0 until  this[i].count())
        if (this[i][j] == a){
            mines.add(Pair(j,i))
            this[i][j] = b
        }
    return mines.toSet()
}
fun Array<Array<Char>>.setMark(mine: Pair<Int, Int>, c: Char){
    this[mine.second][mine.first]=c
}

fun play(map: Array<Array<Char>>, mines: Set<Pair<Int,Int>>){
    var marks: MutableSet<Pair<Int,Int>> = mutableSetOf()
    while (!marks.equals(mines)){
        var set: Pair<Int, Int>? = null
        do{
            print("Set/delete mines marks (x and y coordinates): ")
            val t = readLine()!!.split(" ").map(String::toInt)
            if (t.count()==2 && t[0] in 1..SIZE && t[1] in 1..SIZE ){
                when (map[t[1]][t[0]]){
                    SPACE -> {
                        set = Pair(t[0],t[1])
                        marks.add(set)
                        map.setMark(set, MARK)
                    }
                    MARK ->{
                        set = Pair(t[0],t[1])
                        marks.remove(set)
                        map.setMark(set, SPACE)
                    }
                }
            }
        } while ( set == null)
        map.printMap()
    }
    println()
    println("Congratulations! You founded all mines!\n")
}

fun main(args: Array<String>) {
    val map: Array<Array<Char>> = Array(SIZE + 2, { Array(SIZE + 2, { SPACE }) })
    var n: Int
    do {
        print("How many mines do you want on the field? ")
        n = readLine()!!.toInt()
    } while (n < 0 || n > SIZE * SIZE)
    generateMap(map, n)
    calculateMap(map)
    val mines = map.getMines(MINE,SPACE)
    map.printMap()
    play(map,mines)
}
