package com.example.sudoku

import java.util.Random

class Sudoku {

    private val size = 9
    private var solvable = false;

    private var holes =  Array(size) {
        Array(size, { false})
    }

    private var mistakes = 0
    private val rand = Random()

    private var diff : Int = 0
    private val nGivens = arrayOf(50,36,32,28,22)
    private val nGivensDelta = arrayOf(10,13,3,3,5)
    private val LBRowCol = arrayOf(5,4,3,2,0)

    private var grid = arrayOf(
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
    )

    private var solutionGrid = arrayOf(
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
        arrayOf(-1, -1, -1, -1, -1, -1, -1, -1, -1),
    )


    constructor(difficulty : Int ) {
        this.diff = difficulty
        println("DIFF $diff")
        while(!solvable) {
            generateTerminal(12)
            solveSudoku(0,0,true)
        }
        propagation()
        for (r in 0 until size) {
            for (c in 0 until size) {
                solutionGrid[r][c] = grid[r][c]
            }
        }
        digHoles()
        for (r in 0 until size) {
            for (c in 0 until size) {
                holes[r][c] = ( grid[r][c]==-1 )
            }
        }


    }

    public fun getCell(r: Int, c: Int) : String {
        if(grid[r][c]== -1) {
            return " "
        }
        return grid[r][c].toString()
    }

    public fun setCell(r: Int, c: Int, value : Int) : Boolean {
        if(holes[r][c]) {
            grid[r][c]= value
            val correct = value==solutionGrid[r][c]
            if(!correct) {
                mistakes++
                println(solutionGrid[r][c])
            }
            return correct
        }else {
            return false
        }
    }

    public fun eraseCell(r: Int,c: Int) : Int {
        if(holes[r][c]) {
            grid[r][c] = -1
        }else {
            return -1
        }
        return 0
    }

    public fun isHole(r: Int,c: Int) : Boolean {
        return holes[r][c]
    }

    public fun getMistakes() : Int {
        return mistakes
    }

    public fun isFinished() : Boolean {
        if(mistakes>3) {
            return true
        }
        for (r in 0 until size) {
            for (c in 0 until size) {
                if( grid[r][c] != solutionGrid[r][c] ) {
                    return false
                }
            }
        }
        return true
    }


    private fun digHoles() {
        val givensRows = IntArray(size) {9}
        val givensColumns = IntArray(size) {i -> 9}
        var nHoles = 81 - ( nGivens[diff] + rand.nextInt(nGivensDelta[diff]+1) )

        var canBeDigged = Array(size) {
            Array(size, { true })
        }

        var count = 81
        var r = rand.nextInt(size)
        var c = rand.nextInt(size)
        while (nHoles > 0 && count > 0) {
            println("$r $c : $nHoles")
            if(canBeDigged[r][c] ) {
                if(givensColumns[c]==LBRowCol[diff] || givensRows[r]==LBRowCol[diff] ) {
                    canBeDigged[r][c] = false
                }else {
                    givensColumns[c]--
                    givensRows[r]--
                    val oldValue = grid[r][c]
                    grid[r][c] = -1
                    var noOtherSol = true
                    var value = 1
                    while(noOtherSol && value<=size) {
                        if(value!=oldValue) {
                            grid[r][c] = value
                            if(check(r,c)) {
                                solveSudoku(0,0,false)
                                if(solvable) {
                                    noOtherSol=false
                                }
                            }
                        }
                        value++
                    }
                    if(noOtherSol) {
                        grid[r][c]= -1
                        nHoles--
                        canBeDigged[r][c] = false
                    }else {
                        grid[r][c] = oldValue
                        canBeDigged[r][c] = false
                    }
                }
                count--
            }
            val newCell = nextPos(r,c)
            r = newCell[0]
            c = newCell[1]

            /*c+=2
            if(c>=size) {
                r = (r+1)%size
                c %= size
            }*/

        }
        println("ALL HOLES FOUND")
        

    }

    private fun nextPos(r : Int,c: Int) : Array<Int> {
        var newR = r
        var newC = c
        when(diff) {
            0,1 -> {
                newR = rand.nextInt(size)
                newC = rand.nextInt(size)
            }
            2 -> {
                newC+=2
                if(newC>=size) {
                    newR = (newR+1)%size
                    newC %= size
                }
            }
            3 -> {
                if(newR%2==0) {
                    if(newC<size-1) {
                        newC++
                    }else {
                        newR++
                        if(newR==size) {
                            newC = 0
                        }
                    }
                }else {
                    if(newC>0) {
                        newC--
                    }else { newR++ }
                }
            }
            4 -> {
                newC++
                if(newC==size) {
                    newR++
                    newC = 0
                }
            }
        }
        if(newR>=size) {
            newR %=size
        }

        return  arrayOf(newR,newC)
    }



    private fun checkRow(r: Int): Boolean {
        val incidence = BooleanArray(size)
        for ( i in 0 until size) {
            incidence[i] = false;
        }

        for ( i in 0 until size) {
            val value = grid[r][i]
            if(value!=-1) {
                if(incidence[value-1]) {
                    return false
                }else {
                    incidence[value-1] = true
                }
            }
        }
        return true
    }

    private fun checkColumn(c : Int): Boolean  {
        val incidence = BooleanArray(size)
        for ( i in 0 until size) {
            incidence[i] = false;
        }

        for ( i in 0 until size) {
            val value = grid[i][c]
            if(value!=-1) {
                if(incidence[value-1]) {
                    return false
                }else {
                    incidence[value-1] = true
                }
            }
        }
        return true
    }

    private fun checkBlock(r: Int, c: Int): Boolean {
        val incidence = BooleanArray(size)
        for ( i in 0 until size) {
            incidence[i] = false;
        }
        val shiftL = r %3
        val shiftC = c%3
        val centerL = r - (shiftL-1)
        val centerC = c - (shiftC-1)

        for(i in centerL-1..centerL+1) {
            for(j in centerC-1..centerC+1) {
                val value = grid[i][j]
                if(value!=-1) {
                    if(incidence[value-1]) {
                        return false
                    }else {
                        incidence[value-1] = true
                    }
                }
            }
        }
        return true
    }

    private fun check(r : Int, c : Int) : Boolean {
        return checkBlock(r,c) && checkColumn(c) && checkRow(r);
    }

    private fun permuteArray(a : Array<Int>) {
        //val r = Random()
        val n = a.size
        for(i in 0 until n) {
            val i1 = rand.nextInt(n)
            val i2 = rand.nextInt(n)
            val temp = a[i1]
            a[i1] = a[i2]
            a[i2] = temp
        }
    }


    private fun solveSudoku(r: Int, c: Int, save: Boolean = false)   {
        if(r==0 && c==0) {
            solvable = false
        }
        if(r < size  ) {
            if(grid[r][c]==-1) {
                val values = arrayOf(1,2,3,4,5,6,7,8,9)
                permuteArray(values)
                var valueIndex = 0
                while(valueIndex<size && !solvable) {
                    grid[r][c] = values[valueIndex]
                    if(check(r,c)) {
                        if(c==size-1) {
                            solveSudoku(r +1,0, save)
                        }else {
                            solveSudoku(r,c+1, save)
                        }
                    }
                    if(!solvable || (solvable && !save) ) {
                        grid[r][c] = -1
                    }
                    valueIndex++
                }
            }else {
                if(c==size-1) {
                    solveSudoku(r +1,0, save)
                }else {
                    solveSudoku(r,c+1, save)
                }
            }
        }else {
            solvable = true
        }

    }

    /***
     * Generate terminal grid with Las Vegas algorithm. This grid will be used for digging holes later
     */
    private fun generateTerminal(n : Int) {
        var count = 0
        var r = rand.nextInt(size)
        var c = rand.nextInt(size)
        while(count < n) {
            while(grid[r][c]!=-1) {
                r = rand.nextInt(size)
                c = rand.nextInt(size)
            }
            var index = 0
            var ok = false
            val values = arrayOf(1,2,3,4,5,6,7,8,9)
            permuteArray(values)
            while(index<size && !ok) {
                grid[r][c] = values[index]
                ok = check(r,c)
                index++
            }

            if(ok) {
                count++
            }else{
                grid[r][c] = -1
            }
        }
    }


    private fun propagation() {
        var n =rand.nextInt(5)
        for (i in 0 until n) { exchangeNumber() }

        n =rand.nextInt(3)
        for (i in 0 until n) {
            val b = rand.nextInt(3)
            val c1 = b*3 + rand.nextInt(3)
            val c2 = b*3 +rand.nextInt(3)
            switchColumn(c1,c2)
        }

        n =rand.nextInt(1)
        for (i in 0 until n) { switchBlockColumn() }

        n =rand.nextInt(3)
        for (i in 0 until n) { gridRolling() }
    }

    private fun gridRolling() {
        var copyGrid = Array(size) {
            Array<Int>(size) {
                -1
            }
        }
        for (r in 0 until size) {
            for (c in 0 until size) {
                copyGrid[r][c] = grid[r][c]
            }
        }

        for (r in 0 until size) {
            for (c in 0 until size) {
                grid[c][size-r-1] = copyGrid[r][c]
            }
        }
    }

    private fun switchBlockColumn() {
        var b1 = rand.nextInt(3)
        var b2 = rand.nextInt(3)
        for (i in 0 until 3) {
            switchColumn(b1*3+i,b2*3+i)
        }
    }

    private fun switchColumn(c1: Int, c2: Int) {
       for (r in 0 until size) {
           val temp = grid[r][c1]
           grid[r][c1] = grid[r][c2]
           grid[r][c2] = temp
       }
    }

    private fun exchangeNumber() {
        val n1 = rand.nextInt(size) + 1
        val n2 = rand.nextInt(size) + 1
        for (r in 0 until  size) {
            for (c in 0 until size) {
                if(grid[r][c]==n1 ) {
                    grid[r][c] = n2
                }else if(grid[r][c]==n2 ){
                    grid[r][c] = n1
                }
            }
        }
    }
}