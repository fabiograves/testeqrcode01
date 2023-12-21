package com.rr.ars.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.ScrollView
import androidx.core.content.ContextCompat
import com.rr.ars.R

class CustomGridView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paint = Paint()
    private var productPosition: Pair<Int, Int>? = null

    val prateleira = ContextCompat.getColor(context, R.color.prateleira)
    val divisoria = ContextCompat.getColor(context, R.color.divisoria)

    // Dimensões preferidas
    private val cellSize = 7f // Ajuste isso para caber na tela conforme necessário

    // Zoom e Pan
    private var scaleFactor = 1f
    private var translateX = 0f
    private var translateY = 0f
    private val zoomMatrix = Matrix()

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
            val contentWidth = cellSize * 80 * scaleFactor
            val contentHeight = cellSize * 185 * scaleFactor

            val newTranslateX = translateX - distanceX
            val newTranslateY = translateY - distanceY

            val rightLimit = if (contentWidth > width) (width - contentWidth) else 0f
            val bottomLimit = if (contentHeight > height) (height - contentHeight) else 0f

            translateX = newTranslateX.coerceIn(rightLimit, 0f)
            translateY = newTranslateY.coerceIn(bottomLimit, 0f)

            invalidate()
            return true
        }
    })


    private val scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            val oldScaleFactor = scaleFactor
            scaleFactor *= detector.scaleFactor
            scaleFactor = scaleFactor.coerceIn(0.1f, 10.0f)

            if (scaleFactor != oldScaleFactor) {
                val contentWidth = cellSize * 80 * scaleFactor
                val contentHeight = cellSize * 185 * scaleFactor

                val rightLimit = if (contentWidth > width) (width - contentWidth) else 0f
                val bottomLimit = if (contentHeight > height) (height - contentHeight) else 0f

                translateX = translateX.coerceIn(rightLimit, 0f)
                translateY = translateY.coerceIn(bottomLimit, 0f)
            }

            invalidate()
            return true
        }
    })


    private var parentScrollView: ScrollView? = null

    fun setParentScrollView(scrollView: ScrollView) {
        parentScrollView = scrollView
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parentScrollView?.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                parentScrollView?.requestDisallowInterceptTouchEvent(false)
            }
        }

        scaleGestureDetector.onTouchEvent(event)
        if (!scaleGestureDetector.isInProgress) {
            gestureDetector.onTouchEvent(event)
        }
        return true
    }


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
        zoomMatrix.setTranslate(translateX, translateY)
        zoomMatrix.preScale(scaleFactor, scaleFactor)
        canvas.setMatrix(zoomMatrix)
        super.onDraw(canvas)
        // Desenhar a grade
        for (i in 0..79) {
            for (j in 0..184) {
                // Definir a cor padrão das células
                paint.color = Color.LTGRAY

                // Pintar células de cinza escuro (prateleiras HORIZONTAL)
                if ((i in 2 .. 63 && j in 1 ..3)
                    ) {
                    paint.color = prateleira
                }

                // Pintar células de cinza escuro (prateleiras VERTICAL superior e lateral esq)
                if ((i in 1 .. 3 && j in 11 ..172) ||
                    (i in 11 .. 17 && j in 11 ..64) ||
                    (i in 24 .. 30 && j in 11 ..64) ||
                    (i in 37 .. 43 && j in 11 ..64) ||
                    (i in 50 .. 56 && j in 11 ..64)
                ) {
                    paint.color = prateleira
                }

                // Pintar células de cinza escuro (prateleiras VERTICAL frente TI)
                if ((i in 11 .. 17 && j in 100 ..172) ||
                    (i in 28 .. 30 && j in 100 ..172) ||
                    (i in 41 .. 43 && j in 127 ..172) ||
                    (i in 51 .. 53 && j in 100 ..172)
                ) {
                    paint.color = prateleira
                }

                // Pintar divisao prateleiras (SUPERIOR)
                if ((i == 1 && j in 1..3) ||
                    (i == 10 && j in 1..3) ||
                    (i == 19 && j in 1..3) ||
                    (i == 28 && j in 1..3) ||
                    (i == 37 && j in 1..3) ||
                    (i == 46 && j in 1..3) ||
                    (i == 55 && j in 1..3) ||
                    (i == 64 && j in 1..3)

                    ) {
                    paint.color = divisoria
                }

                // Pintar divisao prateleiras (Lateral ESQ)
                if ((i in 1..3 && j == 10) ||
                    (i in 1..3 && j == 19) ||
                    (i in 1..3 && j == 28) ||
                    (i in 1..3 && j == 37) ||
                    (i in 1..3 && j == 46) ||
                    (i in 1..3 && j == 55) ||
                    (i in 1..3 && j == 64) ||
                    (i in 1..3 && j == 73) ||
                    (i in 1..3 && j == 82) ||
                    (i in 1..3 && j == 91) ||
                    (i in 1..3 && j == 100) ||
                    (i in 1..3 && j == 109) ||
                    (i in 1..3 && j == 118) ||
                    (i in 1..3 && j == 127) ||
                    (i in 1..3 && j == 136) ||
                    (i in 1..3 && j == 145) ||
                    (i in 1..3 && j == 154) ||
                    (i in 1..3 && j == 163) ||
                    (i in 1..3 && j == 172)

                ) {
                    paint.color = divisoria
                }

                // Pintar divisao prateleiras (Superior Meio)
                if (
                    //coluna 1
                    (i == 14 && j in 11 .. 64) ||
                    (i in 11..17 && j == 10) ||
                    (i in 11..17 && j == 19) ||
                    (i in 11..17 && j == 28) ||
                    (i in 11..17 && j == 37) ||
                    (i in 11..17 && j == 46) ||
                    (i in 11..17 && j == 55) ||
                    (i in 11..17 && j == 64) ||
                    //coluna 2
                    (i == 27 && j in 11 .. 64) ||
                    (i in 24..30 && j == 10) ||
                    (i in 24..30 && j == 19) ||
                    (i in 24..30 && j == 28) ||
                    (i in 24..30 && j == 37) ||
                    (i in 24..30 && j == 46) ||
                    (i in 24..30 && j == 55) ||
                    (i in 24..30 && j == 64) ||
                    //coluna 3
                    (i == 40 && j in 11 .. 64) ||
                    (i in 37..43 && j == 10) ||
                    (i in 37..43 && j == 19) ||
                    (i in 37..43 && j == 28) ||
                    (i in 37..43 && j == 37) ||
                    (i in 37..43 && j == 46) ||
                    (i in 37..43 && j == 55) ||
                    (i in 37..43 && j == 64) ||
                    //coluna 4
                    (i == 53 && j in 11 .. 64) ||
                    (i in 50..56 && j == 10) ||
                    (i in 50..56 && j == 19) ||
                    (i in 50..56 && j == 28) ||
                    (i in 50..56 && j == 37) ||
                    (i in 50..56 && j == 46) ||
                    (i in 50..56 && j == 55) ||
                    (i in 50..56 && j == 64)

                ) {
                    paint.color = divisoria
                }

                // Pintar divisao prateleiras (Inferior Meio)
                if (
                    //coluna 1
                    (i == 14 && j in 100 .. 172) ||
                    (i in 11..17 && j == 100) ||
                    (i in 11..17 && j == 109) ||
                    (i in 11..17 && j == 118) ||
                    (i in 11..17 && j == 127) ||
                    (i in 11..17 && j == 136) ||
                    (i in 11..17 && j == 145) ||
                    (i in 11..17 && j == 154) ||
                    (i in 11..17 && j == 163) ||
                    (i in 11..17 && j == 172) ||
                    //coluna 2
                    (i in 28..30 && j == 100) ||
                    (i in 28..30 && j == 109) ||
                    (i in 28..30 && j == 118) ||
                    (i in 28..30 && j == 127) ||
                    (i in 28..30 && j == 136) ||
                    (i in 28..30 && j == 145) ||
                    (i in 28..30 && j == 154) ||
                    (i in 28..30 && j == 163) ||
                    (i in 28..30 && j == 172) ||
                    //coluna 3
                    (i in 41..43 && j == 127) ||
                    (i in 41..43 && j == 136) ||
                    (i in 41..43 && j == 145) ||
                    (i in 41..43 && j == 154) ||
                    (i in 41..43 && j == 163) ||
                    (i in 41..43 && j == 172) ||
                    //coluna 4
                    (i in 51..53 && j == 100) ||
                    (i in 51..53 && j == 109) ||
                    (i in 51..53 && j == 118) ||
                    (i in 51..53 && j == 127) ||
                    (i in 51..53 && j == 136) ||
                    (i in 51..53 && j == 145) ||
                    (i in 51..53 && j == 154) ||
                    (i in 51..53 && j == 163) ||
                    (i in 51..53 && j == 172)

                ) {
                    paint.color = divisoria
                }

                // Pintar paredes/colunas
                if ((i in 0 .. 79 && j == 0) ||
                    (i == 0 && j in 0 .. 184) ||
                    (i in 0 .. 79 && j == 184) ||
                    (i == 79 && j in 0 .. 184)

                    ) {
                    paint.color = Color.BLACK
                }

                // Pintar células específicas de vermelho
                if ((i in 65..78 && j in 1 .. 64) ||
                    (i in 1..38 && j in 175 .. 183)

                    ){
                    paint.color = Color.parseColor("#FF0000")
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

        // Desenhar a posição do produto e células adjacentes
        productPosition?.let { position ->
            val (x, y) = position
            val rangeX = (x - 1)..(x + 1)
            val rangeY = (y - 1)..(y + 1)
            for (i in rangeX) {
                for (j in rangeY) {
                    if (i in 0..79 && j in 0..184) { // Verificar limites para evitar desenhar fora da grade
                        val cellColor = if (isBlinking || (i == x && j == y)) productColor else Color.LTGRAY
                        paint.color = cellColor
                        canvas.drawRect(
                            i * cellSize, j * cellSize,
                            (i + 1) * cellSize, (j + 1) * cellSize, paint
                        )
                    }
                }
            }
        }

        // Agendar o próximo piscar se necessário
        if (isBlinking && blinkCount < maxBlinkCount) {
            postInvalidateDelayed(blinkInterval)
        }
    }
}
