package com.example.pictra

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import com.example.pictra.CustomDrawView.GetCoordinateCallback
import com.example.pictra.CustomDrawView
import androidx.core.content.ContextCompat
import com.example.pictra.R
import android.view.MotionEvent
import java.util.ArrayList

class CustomDrawView(context: Context?, attrs: AttributeSet?) : AppCompatImageView(
    context!!, attrs
) {
    private var state = 0
    private val paintPenList = ArrayList<Paint>()
    private lateinit var latestPath: Path
    private lateinit var latestPaint: Paint
    private val pathPenList = ArrayList<Path>()
    private lateinit var callbackForCoordinate: GetCoordinateCallback
    private var lineWidth = 15
    private var currentColor = 0
    private fun init() {
        DEFAULT_COLOR = ContextCompat.getColor(context, R.color.colorAccent)
        currentColor = DEFAULT_COLOR
        initPaintNPen(currentColor)
    }

    private fun initPaintNPen(color: Int) {
        latestPaint = getNewPaintPen(color)
        latestPath = newPathPen
        paintPenList.add(latestPaint)
        pathPenList.add(latestPath)
    }

    private val newPathPen: Path
        get() = Path()

    private fun getNewPaintPen(color: Int): Paint {
        val mPaintPen = Paint()
        mPaintPen.strokeWidth = lineWidth.toFloat()
        mPaintPen.isAntiAlias = true
        mPaintPen.isDither = true
        mPaintPen.style = Paint.Style.STROKE
        mPaintPen.strokeJoin = Paint.Join.MITER
        mPaintPen.strokeCap = Paint.Cap.ROUND
        mPaintPen.color = color
        return mPaintPen
    }

    fun setThisCallback(callback: GetCoordinateCallback) {
        callbackForCoordinate = callback
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        Log.i("CO-ordinate", event.x.toString() + " : " + event.y)
        if (event.action == MotionEvent.ACTION_DOWN) {
            callbackForCoordinate.start(x, y)
            startPath(x, y)
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            callbackForCoordinate.moving(x, y)
            updatePath(x, y)
        } else if (event.action == MotionEvent.ACTION_UP) {
            callbackForCoordinate.end(x, y)
            endPath(x, y)
        }
        invalidate()
        return true
    }

    private fun startPath(x: Float, y: Float) {
        /*if(state==STATE_MOVING)
            mPath.lineTo(x,y);
        else
            mPath.moveTo(x,y);*/
        initPaintNPen(currentColor)
        latestPath.moveTo(x, y)
    }

    private fun updatePath(x: Float, y: Float) {
        state = STATE_MOVING
        latestPath.lineTo(x, y)
    }

    private fun endPath(x: Float, y: Float) {}
    fun setDrawColor(color: Int) {
        currentColor = color
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        for (i in paintPenList.indices) {
            canvas.drawPath(pathPenList[i], paintPenList[i])
        }
    }

    fun increaseWidth(decrease: Boolean) {
        if (decrease) {
            if (lineWidth > 5) {
                lineWidth -= 10
            }
        } else {
            if (lineWidth < 50) {
                lineWidth += 10
            }
        }
        invalidate()
    }

    fun resetView() {
        currentColor = DEFAULT_COLOR
        state = STATE_STILL
        latestPath.reset()
        latestPaint.reset()
        pathPenList.clear()
        paintPenList.clear()
        lineWidth = 20
        initPaintNPen(currentColor)
        invalidate()
    }

    fun undoPath() {
        if (paintPenList.size > 1) {
            latestPaint = paintPenList[paintPenList.size - 2]
            latestPath = pathPenList[pathPenList.size - 2]
            paintPenList.removeAt(paintPenList.size - 1)
            pathPenList.removeAt(pathPenList.size - 1)
            currentColor = latestPaint.color
            lineWidth = latestPaint.strokeWidth.toInt()
        } else {
            resetView()
        }
        invalidate()
    }

    interface GetCoordinateCallback {
        fun moving(x: Float, y: Float)
        fun start(x: Float, y: Float)
        fun end(x: Float, y: Float)
    }

    companion object {
        private const val STATE_STILL = 0
        private const val STATE_MOVING = 1
        private var DEFAULT_COLOR = 0
    }

    init {
        init()
    }
}