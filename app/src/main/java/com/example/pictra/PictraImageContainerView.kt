package com.example.pictra

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.util.AttributeSet
import kotlin.jvm.JvmOverloads
import android.widget.RelativeLayout
import com.example.pictra.PictraImageView.GetCoordinateCallback
import android.widget.TextView
import android.view.LayoutInflater
import androidx.core.content.ContentProviderCompat.requireContext
import java.io.File
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


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
    private lateinit var uploadBitmapListener: UploadBitmapListener
    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.pictra_image_container_view, this, true)
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

    fun saveImageToFile(file: File) {
        pictraImageView.buildDrawingCache()
        val bitmap = pictraImageView.drawingCache
        uploadBitmapListener.uploadImage(bitmap)
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

    fun setOnUploadListener(imageEditorActivity: UploadBitmapListener) {
        uploadBitmapListener = imageEditorActivity
    }

    init {
        initView()
    }

    private fun saveMediaToStorage(bitmap: Bitmap) {
        //Generating a file name
        val filename = "pictra_image${System.currentTimeMillis()}.jpg"

        //Output stream
        var fos: OutputStream? = null

        //For devices running android >= Q
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //getting the contentResolver
            context?.contentResolver?.also { resolver ->

                //Content resolver will process the contentvalues
                val contentValues = ContentValues().apply {

                    //putting file information in content values
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }

                //Inserting the contentValues to contentResolver and getting the Uri
                val imageUri: Uri? =
                    resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                //Opening an outputstream with the Uri that we got
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            //These for devices running on android < Q
            //So I don't think an explanation is needed here
            val imagesDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }

        fos?.use {
            //Finally writing the bitmap to the output stream that we opened
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
    }
}