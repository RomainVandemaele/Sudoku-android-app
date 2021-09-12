package com.example.sudoku

//import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import kotlinx.android.synthetic.main.game_screen.*


//Blue : #000fb1
//Yellow : #dca217
class GameScreen : AppCompatActivity() {

    //var lines  = arrayOf(l1,l2,l3,l4,l5,l6,l6,l7,l8,l9)
    var selectedText : TextView? = null
    var selectedTextColor : Int = Color.BLACK

    var sudoku : Sudoku? = null
    var cellRow = -1
    var cellColumn = -1

    var runnable : Runnable = Runnable {  }
    var handler : Handler = Handler()
    var running = false

    var startTime : Long = 0L;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.game_screen)

        //val start = System.currentTimeMillis()
        //val time = System.currentTimeMillis() - start
        //Log.i("Time : ", start.toString() + " " + System.currentTimeMillis().toString())
        //Log.i("Time 2 : ", start.toString() + " " + System.currentTimeMillis().toString())

        val difficulty  = intent.getIntExtra("Difficulty",2)
        println(" : $difficulty")
        initSudoku(difficulty)

    }

    private fun initSudoku(difficulty : Int) {
        val start = System.currentTimeMillis()
        sudoku = Sudoku(difficulty)
        var lines  = arrayOf(l1, l2, l3, l4, l5, l6, l7, l8, l9)
        for( i in 0..8) {
            for(j in 0..8) {
                val text = sudoku!!.getCell(i, j)
                val textView = (lines[i][j] as TextView)
                textView.text  = text
                textView.setTextColor(Color.BLACK)
            }
        }
        start()
    }

    fun selectCase(view: View) {
        if(selectedText!=null) {
            //selectedText!!.setBackgroundColor(selectedTextBG)
            if(sudoku!!.isHole(cellRow,cellColumn)) {
                selectedText?.setTextColor(Color.GRAY)
                selectedText?.text= sudoku!!.getCell(cellRow, cellColumn)
            }else {
                selectedText?.setTextColor(Color.BLACK)
            }
        }

        val text : TextView = view as TextView
        selectedText = text
        //selectedTextColor = selectedText!!.textColors

        val pos: String = text.tag.toString()
        cellRow = Integer.parseInt(pos[1].toString()) - 1
        cellColumn =  Integer.parseInt(pos[2].toString()) - 1

        if( sudoku!!.isHole(cellRow,cellColumn)  && selectedText!!.text.toString() == " "  ) {
            selectedText?.text="?"
        }
        selectedText!!.setTextColor(Color.BLUE)

        //Log.i("test", "click on $cellRow $cellColumn")
        //println(selectedTextBG.toString())
    }

    fun selectNumber(view: View) {
        if(selectedText!=null && sudoku!!.isHole(cellRow,cellColumn)) {
            val button = view as Button
            val value = Integer.parseInt(button.text.toString())
            val res = sudoku?.setCell(cellRow,cellColumn,value)
            selectedText!!.text = value.toString()
            if(res==false) {
                selectedText!!.setTextColor(Color.RED)
            }else {
                selectedText!!.setTextColor(Color.BLUE)
            }

            if(sudoku!!.isFinished()) {

                endOfGame()
            }
        }
    }

    private fun endOfGame() {
        handler.removeCallbacks(runnable)
        var msg = ""
        if(sudoku!!.getMistakes() > 3) {
            msg = "Sorry too many mistakes : try again"
        }else {
            val completionTime = timer.text.toString().subSequence(7,12)
            msg = "Congratulation for solving this sudoku in $completionTime"
        }
        val alertDialog: AlertDialog? = this?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                //setMessage("Facile?")
                setTitle("Fin de partie")
                setMessage(msg)
                setPositiveButton("OK",
                DialogInterface.OnClickListener { dialog, id ->
                    val intent = Intent(applicationContext,LaunchActivity::class.java)
                    startActivity(intent)
                })

            }
            builder.create()
        }
        alertDialog!!.show()
    }

    fun erase(view: View) {
        if(selectedText!=null && sudoku!!.isHole(cellRow,cellColumn)) {
            //println("ERASE POSSIBLE")
            sudoku!!.eraseCell(cellRow,cellColumn)
            selectedText!!.text = sudoku!!.getCell(cellRow,cellColumn)
            selectedText!!.text = " "
        }
    }

    private fun start() {
        startTime = System.currentTimeMillis()
        runnable = object : Runnable {
            var lastIndex = 0
            override fun run() {
                var elapsedTime = System.currentTimeMillis() - startTime
                elapsedTime /=1000
                val min = elapsedTime/60
                val sec = elapsedTime%60
                var timerText = "Time : "
                if(min<10) { timerText+="0" }
                timerText += "$min:"
                if(sec<10) { timerText+="0" }
                timerText += "$sec"
                timer.text = timerText

                val nMistakes = sudoku!!.getMistakes()
                mistakesView.text  = "Mistakes : $nMistakes/3"
                handler.postDelayed(this,500)
            }
        }
        handler.post(runnable)
    }
}