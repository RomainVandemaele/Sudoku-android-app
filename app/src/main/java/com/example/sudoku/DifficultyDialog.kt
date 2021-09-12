package com.example.sudoku

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle

import androidx.fragment.app.DialogFragment


class DifficultyDialog : DialogFragment() {

    val difficultyArray = arrayOf("Débutant","Facile","Moyen","Difficile","Expert")

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("dialog fire missiles")
                .setTitle("Choose difficulty")
                .setItems(difficultyArray,
                    DialogInterface.OnClickListener { dialog, which ->
                    // The 'which' argument contains the index position
                    // of the selected item
                })
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User confirm his/her choice
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    override fun onDismiss(dialog: DialogInterface) { //When dialog disapears like when you press a button/list item
        super.onDismiss(dialog)
    }

    override fun onCancel(dialog: DialogInterface) { //when user left dialog like press return or click outside dialog
        super.onCancel(dialog)
    }
}
