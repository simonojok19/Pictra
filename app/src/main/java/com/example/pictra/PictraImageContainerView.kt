package com.example.pictra

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import kotlin.jvm.JvmOverloads
import android.widget.RelativeLayout
import com.example.pictra.PictraImageView.GetCoordinateCallback
import android.widget.TextView
import android.view.LayoutInflater

class PictraImageContainerView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), GetCoordinateCallback {
    private lateinit var pictraImageView: PictraImageView
    private lateinit var startText: TextView
    private lateinit var moveText: TextView
    private lateinit var endText: TextView
    private var isDebugEnabled = true
    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.custom_canvas_for_draw, this, true)
        pictraImageView = findViewById(R.id.mainView)
        startText = findViewById(R.id.startPointText)
        moveText = findViewById(R.id.movingPointText)
        endText = findViewById(R.id.endPointText)
        if (isDebugEnabled) {
            startText.visibility = VISIBLE
            moveText.visibility = VISIBLE
            endText.visibility = VISIBLE
        } else {
            startText.visibility = GONE
            moveText.visibility = GONE
            endText.visibility = GONE
        }
        pictraImageView.setThisCallback(this)
    }

    fun setDebugMode(isDebugEnabled: Boolean) {
        this.isDebugEnabled = isDebugEnabled
    }

    fun changeColor(color: Int) {
        pictraImageView.setDrawColor(color)
    }

    fun undoView() {
        pictraImageView.undoPath()
    }

    fun increaseWidth(decrease: Boolean) {
        pictraImageView.increaseWidth(decrease)
    }

    fun resetView() {
        pictraImageView.resetView()
        moveText.text = "0.0"
        startText.text = "0.0"
        endText.text = "0.0"
    }

    override fun moving(x: Float, y: Float) {
        moveText.text = String.format("%.02f", x) + ", " + String.format("%.02f", y)
    }

    override fun start(x: Float, y: Float) {
        startText.text = String.format("%.02f", x) + ", " + String.format("%.02f", y)
    }

    override fun end(x: Float, y: Float) {
        endText.text = String.format("%.02f", x) + ", " + String.format("%.02f", y)
    }

    fun setBitmap (bitmap: Bitmap) {
        pictraImageView.setImageBitmap(bitmap)
    }

    init {
        initView()
    }
}