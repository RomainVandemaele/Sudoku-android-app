package com.example.sudoku

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View


class LaunchActivity : AppCompatActivity() {

    var difficulty = 0;
    val difficultyArray = arrayOf("Débutant","Facile","Moyen","Difficile","Expert")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        var x = DifficultyDialog()
        x.show(supportFragmentManager,"yo")

    }

    fun startGame(view: View) {

        val alertDialog: AlertDialog? = this?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                //setMessage("Facile?")
                setTitle("Choisissez la difficulté")
                setItems(difficultyArray,
                    DialogInterface.OnClickListener { dialog, which ->
                        difficulty = which
                        val intent = Intent(applicationContext,GameScreen::class.java)
                        intent.putExtra("Difficulty",difficulty)
                        startActivity(intent)
                    })

            }
            builder.create()
        }

        alertDialog?.show()

    }
}