package com.example.finalexamapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Afficher un Toast
        Toast.makeText(this, "Bienvenue sur FinalExamApp!", Toast.LENGTH_LONG).show()

        // Afficher un Snackbar
        val rootView = findViewById<View>(android.R.id.content) // Récupération de la vue racine
        Snackbar.make(rootView, "Voici un exemple de Snackbar", Snackbar.LENGTH_SHORT).show()
    }
}
