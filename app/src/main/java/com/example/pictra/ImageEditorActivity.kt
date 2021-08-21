package com.example.pictra

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.net.Uri
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.Exception


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

        val url = intent.data;
        val bitmap = decodeUriToBitmap(this, url!!)
        pictraImageContainerView.setBitmap(bitmap)

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

    fun decodeUriToBitmap(mContext: Context, sendUri: Uri): Bitmap {
        var getBitmap: Bitmap? = null
        try {
            val image_stream: InputStream
            try {
                image_stream = mContext.getContentResolver().openInputStream(sendUri)!!
                getBitmap = BitmapFactory.decodeStream(image_stream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getBitmap!!
    }
}