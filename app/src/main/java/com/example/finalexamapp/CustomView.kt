package com.example.finalexam

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // Méthode onDraw pour dessiner sur la vue personnalisée
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Définir la couleur de fond de la vue
        canvas?.drawColor(Color.CYAN)

        // Créer un objet Paint pour dessiner du texte
        val paint = Paint().apply {
            color = Color.BLACK // Couleur du texte
            textSize = 50f      // Taille du texte
        }

        // Dessiner le texte sur la vue
        canvas?.drawText("Vue personnalisée!", 50f, 50f, paint)
    }
}
