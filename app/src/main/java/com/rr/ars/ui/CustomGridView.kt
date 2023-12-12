package com.rr.ars.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View

class CustomGridView(context: Context) : View(context) {
    private val paint = Paint()
    private var productPosition: Pair<Int, Int>? = null

    // Dimensões preferidas
    private val cellSize = 100f // Ajuste isso para caber na tela conforme necessário

    fun setProductPosition(x: Int, y: Int) {
        productPosition = Pair(x, y)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Desenhar a grade
        for (i in 0..18) { // X de 0 a 18
            for (j in 0..26) { // Y de 0 a 26
                paint.style = Paint.Style.FILL

                // Definir a cor padrão das células
                paint.color = Color.LTGRAY

                // Pintar células específicas de vermelho
                if ((i in 0..8 && j in 24..26) || (i in 14..18 && j in 0..7)) {
                    paint.color = Color.RED
                }

                // Desenhar a célula
                canvas.drawRect(
                    i * cellSize, j * cellSize,
                    (i + 1) * cellSize, (j + 1) * cellSize, paint
                )
            }
        }

        // Destacar a posição do produto com um quadrado verde
        productPosition?.let {
            paint.color = Color.GREEN
            canvas.drawRect(
                it.first * cellSize, it.second * cellSize,
                (it.first + 1) * cellSize, (it.second + 1) * cellSize, paint
            )
        }
    }
}
