package com.example.pictra

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog

class DrawFromFingerActivity : AppCompatActivity() {
    private lateinit var customCanvasForDraw: CustomCanvasForDraw
    private lateinit var resetButton: Button
    private lateinit var sizeMinusButton: Button
    private lateinit var sizePlusButton: Button
    private lateinit var colorButton: Button
    private lateinit var undoButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_from_finger)
        customCanvasForDraw = findViewById(R.id.customCanvas)
        resetButton = findViewById(R.id.resetButton)
        sizeMinusButton = findViewById(R.id.sizeMinusButton)
        sizePlusButton = findViewById(R.id.sizePlusButton)
        colorButton = findViewById(R.id.colorButton)
        undoButton = findViewById(R.id.undoButton)
        customCanvasForDraw.setDebugMode(true)
        listeners()
    }

    private fun listeners() {
        resetButton.setOnClickListener { resetView() }
        sizeMinusButton.setOnClickListener { customCanvasForDraw.increaseWidth(true) }
        sizePlusButton.setOnClickListener { customCanvasForDraw.increaseWidth(false) }
        colorButton.setOnClickListener {
            val dialog = ColorChooserDialog(this@DrawFromFingerActivity)
            dialog.setTitle("Select Color")
            dialog.setColorListener { _, color -> //do whatever you want to with the values
                customCanvasForDraw.changeColor(color)
            }
            //customize the dialog however you want
            dialog.show()
        }
        undoButton.setOnClickListener { customCanvasForDraw.undoView() }
    }

    private fun resetView() {
        customCanvasForDraw.resetView()
    }
}