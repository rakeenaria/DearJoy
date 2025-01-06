package com.example.dearjoy.DrawingView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var backgroundImage: Bitmap? = null
    private val paths = mutableListOf<Path>()
    private val paints = mutableListOf<Paint>()

    private var currentPath = Path()
    private var currentPaint = Paint()

    init {
        setupDefaultPaint()
    }

    private fun setupDefaultPaint(){
        currentPaint.isAntiAlias = true
        currentPaint.color = Color.BLACK
        currentPaint.style = Paint.Style.STROKE
        currentPaint.strokeJoin = Paint.Join.ROUND
        currentPaint.strokeWidth = 10f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Menggambar background jika ada
        if (backgroundImage != null) {
            canvas.drawBitmap(backgroundImage!!, 0f, 0f, null)
        }

        // Menggambar path dan paint yang sudah ada
        for (i in paths.indices) {
            canvas.drawPath(paths[i], paints[i])
        }

        // Menggambar path yang sedang digambar
        canvas.drawPath(currentPath, currentPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentPath = Path()
                currentPath.moveTo(x, y)
                paths.add(currentPath)

                val newPaint = Paint(currentPaint)
                paints.add(newPaint)
                return true
            }

            MotionEvent.ACTION_MOVE -> {
                currentPath.lineTo(x, y)
                invalidate() // Meminta redraw untuk menggambar path yang sedang digambar
                return true
            }

            else -> return super.onTouchEvent(event)
        }
    }

    fun pen(){
        currentPaint.color = Color.BLACK
    }

    fun erase(){
        currentPaint.color = Color.parseColor("#F7F4F2")
    }

    // Mengubah warna pena
    fun setColor(color: Int) {
        if(currentPaint.xfermode == null){
            currentPaint.color = color
        }
    }

    // Mengubah ketebalan pena
    fun setStrokeWidth(width: Float) {
        currentPaint.strokeWidth = width
    }

    // Membersihkan semua path, paint, dan gambar latar belakang
    fun clear() {
        paths.clear()
        paints.clear()

        // Reset currentPath dan currentPaint
        currentPath.reset()
        invalidate() // Meminta redraw untuk menghapus semuanya

    }

    // Mendapatkan bitmap dari gambar yang telah digambar
    fun getBitmap(): Bitmap {
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)

        for (i in paths.indices) {
            canvas.drawPath(paths[i], paints[i])
        }

        return bitmap
    }
}
