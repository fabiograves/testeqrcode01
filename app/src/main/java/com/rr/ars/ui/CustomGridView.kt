package com.rr.ars.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class CustomGridView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private var productPosition: Pair<Int, Int>? = null

    // Dimensões preferidas
    private val cellSize = 50f // Ajuste isso para caber na tela conforme necessário

    // Pisca pisca
    private var isBlinking = false
    private var blinkCount = 0
    private var blinkInterval = 1000L // Intervalo em milissegundos
    private val maxBlinkCount = 20 // Número total de piscadas

    // Hachuras
    val hatchPaint = Paint().apply {
        color = Color.BLACK // Cor das hachuras
        strokeWidth = 2f // Espessura da linha das hachuras
    }


    fun setProductPosition(x: Int, y: Int) {
        productPosition = Pair(x, y)
        invalidate()
    }

    fun startBlinking(x: Int, y: Int) {
        productPosition = Pair(x, y)
        isBlinking = true
        blinkCount = 0
        postInvalidateDelayed(blinkInterval)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // Desenhar a grade
        for (i in 0..18) {
            for (j in 0..26) {
                // Definir a cor padrão das células
                paint.color = Color.LTGRAY

                // Pintar células específicas de vermelho
                if ((i in 0..8 && j in 24..26) || (i in 14..18 && j in 0..7)) {
                    paint.color = Color.RED
                    canvas.drawRect(
                        i * cellSize, j * cellSize,
                        (i + 1) * cellSize, (j + 1) * cellSize, paint
                    )

                    // Desenhar hachuras
                    val startX = i * cellSize
                    val startY = j * cellSize
                    val endX = (i + 1) * cellSize
                    val endY = (j + 1) * cellSize
                    val hatchSpacing = 10f // Ajuste o espaçamento das hachuras

                    for (k in 0..((endX - startX) / hatchSpacing).toInt()) {
                        canvas.drawLine(startX + k * hatchSpacing, startY, startX + k * hatchSpacing, endY, hatchPaint)
                        canvas.drawLine(startX, startY + k * hatchSpacing, endX, startY + k * hatchSpacing, hatchPaint)
                    }
                }

                // Pintar células de cinza escuro
                if ((i in 0 .. 13 && j == 0) ||
                    (i == 0 && j in 2 .. 23) ||
                    (i in 2 .. 3 && j in 2 .. 7) ||
                    (i in 5 .. 6 && j in 2 .. 7) ||
                    (i in 8 .. 9 && j in 2 .. 7) ||
                    (i in 11 .. 12 && j in 2 .. 7) ||
                    (i in 2 .. 3 && j in 12 .. 23) ||
                    (i == 6 && j in 12 .. 23) ||
                    (i == 8 && j in 15 .. 23) ||
                    (i == 12 && j in 12 .. 23) ||
                    (i == 14 && j in 12 .. 23)) {
                    paint.color = Color.DKGRAY
                }

                // Desenhar a célula
                canvas.drawRect(
                    i * cellSize, j * cellSize,
                    (i + 1) * cellSize, (j + 1) * cellSize, paint
                )
            }
        }

        // Determinar a cor do quadrado do produto
        var productColor = Color.LTGRAY // Cor padrão
        if (isBlinking) {
            blinkCount++
            productColor = if (blinkCount % 2 == 0) Color.WHITE else Color.GREEN
            if (blinkCount >= maxBlinkCount) {
                isBlinking = false // Para de piscar após o máximo de piscadas
                productColor = Color.GREEN // Cor final após piscar
            }
        } else if (productPosition != null) {
            productColor = Color.GREEN // Cor se não estiver piscando
        }

        // Desenhar a posição do produto
        productPosition?.let {
            paint.color = productColor
            canvas.drawRect(
                it.first * cellSize, it.second * cellSize,
                (it.first + 1) * cellSize, (it.second + 1) * cellSize, paint
            )
        }

        // Agendar o próximo piscar se necessário
        if (isBlinking && blinkCount < maxBlinkCount) {
            postInvalidateDelayed(blinkInterval)
        }
    }
}
