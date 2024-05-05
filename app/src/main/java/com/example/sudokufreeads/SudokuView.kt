package com.example.sudokufreeads

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.graphics.Bitmap
import com.example.sudokufreeads.R
import kotlinx.android.synthetic.main.sudokumode.*
import kotlinx.android.synthetic.main.sudokumode.view.*
import kotlin.math.min


class SudokuView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    var scaleFactor = .97F
    var scaleCellSide = .1044F
    var cellSide = 90f

    var SudokuDelegate: SudokuDelegate? = null

    private var originX = 20f
    private var originY = 20f
    private var lightcolor = Color.parseColor("#FF0000")
    private var sudokunumbers = resources.getColor(R.color.sudokunumbers)
    private var sudokuboard = resources.getColor(R.color.sudokuboard)
    private var boardlines = resources.getColor(R.color.boardlines)
    private var blanksquare = resources.getColor(R.color.blanksquare)
    private var sudokusquares = resources.getColor(R.color.sudokusquares)
    private var numbersquare = resources.getColor(R.color.numbersquare)
    private var colorbutton = resources.getColor(R.color.colorbutton)
    private var bluenumber = Color.parseColor("#0565A8")
    private var rednumber = Color.parseColor("#ff3333")
    private val paint = Paint()

    private val bitmaps = mutableMapOf<Int, Bitmap>()


    var numbersize = 0f

    var number = 0


    var fromCol: Int = -10
    var fromRow: Int = -10


    init {

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event ?: return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {

                fromCol = ((event.x - originX) / cellSide).toInt()
                fromRow = ((event.y - originY) / cellSide).toInt()

                //Log.d(ContentValues.TAG, "${fromCol},${fromRow}")

                if (fromCol >= 0 && fromCol <= 8 && fromRow >= 0 && fromRow <= 8) {
                    SudokuDelegate?.sudokuColorSquare(fromCol, fromRow)
                }

                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                fromCol = ((event.x - originX) / cellSide).toInt()
                fromRow = ((event.y - originY) / cellSide).toInt()

                if (fromCol >= 0 && fromCol <= 8 && fromRow >= 0 && fromRow <= 8) {
                    SudokuDelegate?.sudokuColorSquare(fromCol, fromRow)
                }
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                val col = ((event.x - originX) / cellSide).toInt()
                val row = ((event.y - originY) / cellSide).toInt()
                fromCol = col
                fromRow = row

                if (fromCol >= 0 && fromCol <= 8 && fromRow >= 0 && fromRow <= 8) {
                    SudokuDelegate?.sudokuColorSquare(fromCol, fromRow)
                }

                invalidate()
            }

        }
        return true
    }


    override fun onDraw(canvas: Canvas?) {
        canvas ?: return

        //Log.d(ContentValues.TAG,"$scaleFactor")
        val GoBoardside = min(width, sudoku_view.height) * scaleFactor
        //cellSide=240f*min(width,height)/ max(width, height)
        originX = (width - GoBoardside)
        originY = (min(width, sudoku_view.height) - GoBoardside)
        cellSide = min(width, sudoku_view.height) * scaleCellSide

        drawBoardSquare(canvas)

        ColorSquare(canvas)
        drawNumberPlayer(canvas)
        drawNumberProblem(canvas)
        drawNumberWrongPlayer(canvas)
        drawNumberProb(canvas)

        drawBoardLines(canvas)


    }


    private fun drawBoardSquareAt(canvas: Canvas, col: Int, row: Int) {
        paint.color = sudokuboard
        canvas.drawRect(
            originX + col * cellSide,
            originY + row * cellSide,
            originX + (col + 1) * cellSide,
            originY + (row + 1) * cellSide,
            paint
        )


    }


    private fun drawFailSquareAt(canvas: Canvas, col: Int, row: Int) {

        canvas.drawRect(
            originX + col * cellSide - cellSide / 2 + cellSide / 48,
            originY + row * cellSide - cellSide / 2 + cellSide / 48,
            originX + (col + 1) * cellSide - cellSide / 2 + cellSide / 48,
            originY + (row + 1) * cellSide - cellSide / 2 + cellSide / 48, paint
        )
    }


    private fun drawBoardSquare(canvas: Canvas) {

        //canvas.drawRect(originX/4 , originY/4  , originX + 19*cellSide, originY + 19*cellSide, paint)
        for (row in 0..8) {
            for (col in 0..8) {
                drawBoardSquareAt(canvas, col, row)


            }
        }

        //val bitmap2=bitmaps[R.drawable.whitepiece]!!
        //canvas.drawBitmap(bitmap2, null, RectF(originX/2 , originY/2  , originX/2 + cellSide, originY/2 + cellSide), paint)
        //val bitmap3=bitmaps[R.drawable.blackpiece]!!
        //canvas.drawBitmap(bitmap3, null, RectF(originX/2 + cellSide , originY/2  , originX/2 + 2*cellSide, originY/2 + cellSide), paint)

    }

    private fun drawBoardLines(canvas: Canvas) {

        //canvas.drawRect(originX/4 , originY/4  , originX + 19*cellSide, originY + 19*cellSide, paint)
        for (row in 0..8) {
            for (col in 0..8) {
                drawLinesAt(canvas, col, row)


            }
        }

        //val bitmap2=bitmaps[R.drawable.whitepiece]!!
        //canvas.drawBitmap(bitmap2, null, RectF(originX/2 , originY/2  , originX/2 + cellSide, originY/2 + cellSide), paint)
        //val bitmap3=bitmaps[R.drawable.blackpiece]!!
        //canvas.drawBitmap(bitmap3, null, RectF(originX/2 + cellSide , originY/2  , originX/2 + 2*cellSide, originY/2 + cellSide), paint)

    }

    private fun ColorSquare(canvas: Canvas) {
        SudokuGame.sudokuColorSquare.forEach { e ->
            var row = e.row
            var col = e.col
            paint.color = sudokusquares
            for (i in 0..8) {
                for (j in 0..8) {
                    canvas.drawRect(
                        originX + i * cellSide + cellSide / 60,
                        originY + row * cellSide + cellSide / 60,
                        originX + (i + 1) * cellSide - cellSide / 60,
                        originY + (row + 1) * cellSide - cellSide / 60,
                        paint
                    )
                    canvas.drawRect(
                        originX + col * cellSide + cellSide / 60,
                        originY + j * cellSide + cellSide / 60,
                        originX + (col + 1) * cellSide - cellSide / 60,
                        originY + (j + 1) * cellSide - cellSide / 60,
                        paint
                    )
                    if (i <= 2 && j <= 2) {
                        canvas.drawRect(
                            originX + ((col / 3) * 3 + i) * cellSide + cellSide / 60,
                            originY + ((row / 3) * 3 + j) * cellSide + cellSide / 60,
                            originX + ((col / 3) * 3 + i + 1) * cellSide - cellSide / 60,
                            originY + ((row / 3) * 3 + j + 1) * cellSide - cellSide / 60,
                            paint
                        )
                    }

                }

            }
            if (SudokuDelegate?.numberat(col, row) == null && SudokuDelegate?.numberproblemat(
                    col,
                    row
                ) == null
            ) {
                paint.color = blanksquare
            } else {
                paint.color = numbersquare
                SudokuGame.numbers.forEach { e ->
                    if (e.number == SudokuDelegate?.findnumber(col, row)!!.number) {
                        canvas.drawRect(
                            originX + e.col * cellSide + cellSide / 60,
                            originY + e.row * cellSide + cellSide / 60,
                            originX + (e.col + 1) * cellSide - cellSide / 60,
                            originY + (e.row + 1) * cellSide - cellSide / 60,
                            paint
                        )
                    }
                }

            }
            canvas.drawRect(
                originX + col * cellSide + cellSide / 60,
                originY + row * cellSide + cellSide / 60,
                originX + (col + 1) * cellSide - cellSide / 60,
                originY + (row + 1) * cellSide - cellSide / 60,
                paint
            )
        }
        invalidate()
    }

    private fun drawNumberProb(canvas: Canvas) {
        paint.textSize = cellSide / 4
        paint.isAntiAlias = true
        var numberproblemspace = (cellSide - paint.textSize) / 3



            SudokuGame.numberprob.forEach { f ->
                SudokuGame.sudokuColorSquare.forEach { e ->
                    var numberprob = SudokuDelegate?.findnumber(e.col, e.row)?.number
                    if (f.number == numberprob) {
                        paint.color = colorbutton
                        if (numberprob > 6) {
                            canvas.drawRect(
                                originX + f.col * cellSide + (numberprob - 1 - (numberprob * 2 / 7) * 3) * numberproblemspace * 4 / 3,
                                originY + (f.row + 1) * cellSide - numberproblemspace * (1 / 4f + (3 - numberprob * 2 / 7) * 5 / 4f),
                                originX + f.col * cellSide + (numberprob - (numberprob * 2 / 7) * 3) * numberproblemspace * 4 / 3,
                                originY + (f.row + 1) * cellSide,
                                paint
                            )
                        } else {
                            canvas.drawRect(
                                originX + f.col * cellSide + (numberprob - 1 - (numberprob * 2 / 7) * 3) * numberproblemspace * 4 / 3,
                                originY + (f.row + 1) * cellSide - numberproblemspace * (1 / 4f + (3 - numberprob * 2 / 7) * 5 / 4f),
                                originX + f.col * cellSide + (numberprob - (numberprob * 2 / 7) * 3) * numberproblemspace * 4 / 3,
                                originY + (f.row + 1) * cellSide - numberproblemspace * (1 / 4f + (2 - numberprob * 2 / 7) * 5 / 4f),
                                paint
                            )
                        }


                    }
                }

                if (f.number <= 3) {
                    paint.color = sudokunumbers
                    canvas.drawText(
                        "${f.number}",
                        originX + f.col * cellSide + numberproblemspace / 3 + (f.number - 1) * numberproblemspace * 4 / 3,
                        originY + (f.row + 1) * cellSide - 3 * numberproblemspace,
                        paint
                    )
                } else if (f.number <= 6) {
                    paint.color = sudokunumbers
                    canvas.drawText(
                        "${f.number}",
                        originX + f.col * cellSide + numberproblemspace / 3 + (f.number - 4) * numberproblemspace * 4 / 3,
                        originY + (f.row + 1) * cellSide - numberproblemspace * 7 / 4,
                        paint
                    )
                } else {
                    paint.color = sudokunumbers
                    canvas.drawText(
                        "${f.number}",
                        originX + f.col * cellSide + numberproblemspace / 3 + (f.number - 7) * numberproblemspace * 4 / 3,
                        originY + (f.row + 1) * cellSide - numberproblemspace / 2,
                        paint
                    )
                }
        }
        invalidate()

    }

    private fun drawNumberProblem(canvas: Canvas) {
        paint.color = sudokunumbers
        paint.textSize = numbersize
        //Log.d(ContentValues.TAG, "${numbersize}")
        paint.isAntiAlias = true
        var numberproblemspace = cellSide - paint.textSize
        SudokuGame.numbersproblem.forEach { e ->
            canvas.drawText(
                "${e.number}",
                originX + e.col * cellSide + numberproblemspace/2+paint.textSize/6,
                originY + (e.row + 1) * cellSide - numberproblemspace/2-paint.textSize/6,
                paint
            )
        }
    }

    private fun drawNumberPlayer(canvas: Canvas) {
        paint.color = bluenumber
        paint.textSize = numbersize
        paint.isAntiAlias = true
        var numberproblemspace = cellSide - paint.textSize
        SudokuGame.numbersplayer.forEach { e ->
            canvas.drawText(
                "${e.number}",
                originX + e.col * cellSide + numberproblemspace/2+paint.textSize/6,
                originY + (e.row + 1) * cellSide - numberproblemspace/2-paint.textSize/6,
                paint
            )
        }

    }

    private fun drawNumberWrongPlayer(canvas: Canvas) {
        paint.color = rednumber
        paint.textSize = numbersize
        paint.isAntiAlias = true
        var numberproblemspace = cellSide - paint.textSize
        SudokuGame.numberswrongplayer.forEach { e ->


            canvas.drawText(
                "${e.number}",
                originX + e.col * cellSide + numberproblemspace/2+paint.textSize/6,
                originY + (e.row + 1) * cellSide - numberproblemspace/2-paint.textSize/6,
                paint
            )
        }
        invalidate()

    }


    private fun drawLinesAt(canvas: Canvas, col: Int, row: Int) {
        paint.color = boardlines

        if (row == 0 || row == 3 || row == 6 || row == 8) {
            if (row == 8) {
                canvas.drawRect(
                    originX - cellSide / 30,
                    originY + (row + 1) * cellSide - cellSide / 30,
                    originX + 9 * cellSide,
                    originY + (row + 1) * cellSide + cellSide / 30,
                    paint
                )
            } else {
                canvas.drawRect(
                    originX - cellSide / 30,
                    originY + row * cellSide - cellSide / 30,
                    originX + 9 * cellSide,
                    originY + row * cellSide + cellSide / 30,
                    paint
                )
            }


        }
        if (col == 0 || col == 3 || col == 6 || col == 8) {
            if (col == 8) {
                canvas.drawRect(
                    originX + (col + 1) * cellSide - cellSide / 30,
                    originY - cellSide / 30,
                    originX + (col + 1) * cellSide + cellSide / 30,
                    originY + 9 * cellSide,
                    paint
                )
            } else {
                canvas.drawRect(
                    originX + col * cellSide - cellSide / 30,
                    originY - cellSide / 30,
                    originX + col * cellSide + cellSide / 30,
                    originY + 9 * cellSide,
                    paint
                )
            }

        }
        canvas.drawRect(
            originX + col * cellSide,
            originY + row * cellSide - cellSide / 60,
            originX + (col + 1) * cellSide,
            originY + row * cellSide + cellSide / 60,
            paint
        )
        canvas.drawRect(
            originX + col * cellSide - cellSide / 60,
            originY + row * cellSide,
            originX + col * cellSide + cellSide / 60,
            originY + (row + 1) * cellSide,
            paint
        )
        canvas.drawRect(
            originX + col * cellSide,
            originY + (row + 1) * cellSide - cellSide / 60,
            originX + (col + 1) * cellSide,
            originY + (row + 1) * cellSide + cellSide / 60,
            paint
        )
        canvas.drawRect(
            originX + (col + 1) * cellSide - cellSide / 60,
            originY + row * cellSide,
            originX + (col + 1) * cellSide + cellSide / 60,
            originY + (row + 1) * cellSide,
            paint
        )
        invalidate()
    }


    fun numberadd() {
        number++
        invalidate()
        //Log.d(ContentValues.TAG, "number : $number")
    }

    fun numberzero() {
        number = 0
        invalidate()
    }
    fun smalltext(){
        numbersize=cellSide*8/16
        invalidate()
    }
    fun mediumtext(){
        numbersize=cellSide*10/16
        invalidate()
    }
    fun largetext(){
        numbersize=cellSide*12/16
        invalidate()
    }


}

