package com.example.pictra

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog

class ImageEditorActivity : AppCompatActivity() {
    private lateinit var pictraImageContainerView: PictraImageContainerView
    private lateinit var resetButton: Button
    private lateinit var sizeMinusButton: Button
    private lateinit var sizePlusButton: Button
    private lateinit var colorButton: Button
    private lateinit var undoButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_draw_from_finger)
        pictraImageContainerView = findViewById(R.id.customCanvas)
        resetButton = findViewById(R.id.resetButton)
        sizeMinusButton = findViewById(R.id.sizeMinusButton)
        sizePlusButton = findViewById(R.id.sizePlusButton)
        colorButton = findViewById(R.id.colorButton)
        undoButton = findViewById(R.id.undoButton)
        pictraImageContainerView.setDebugMode(true)
        listeners()


    }

    private fun listeners() {
        resetButton.setOnClickListener { resetView() }
        sizeMinusButton.setOnClickListener { pictraImageContainerView.increaseWidth(true) }
        sizePlusButton.setOnClickListener { pictraImageContainerView.increaseWidth(false) }
        colorButton.setOnClickListener {
            val dialog = ColorChooserDialog(this@ImageEditorActivity)
            dialog.setTitle("Select Color")
            dialog.setColorListener { _, color -> //do whatever you want to with the values
                pictraImageContainerView.changeColor(color)
            }
            //customize the dialog however you want
            dialog.show()
        }
        undoButton.setOnClickListener { pictraImageContainerView.undoView() }
    }

    private fun resetView() {
        pictraImageContainerView.resetView()
    }
}