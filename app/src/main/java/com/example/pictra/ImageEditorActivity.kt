package com.example.pictra

import android.R.attr
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.turkialkhateeb.materialcolorpicker.ColorChooserDialog
import android.graphics.BitmapFactory

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.lang.Exception
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask

import com.google.android.gms.tasks.OnSuccessListener

import androidx.annotation.NonNull

import com.google.android.gms.tasks.OnFailureListener

import com.google.firebase.storage.StorageReference

import android.R.attr.bitmap
import android.util.Log
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*


class ImageEditorActivity : AppCompatActivity(), UploadBitmapListener {
    private lateinit var pictraImageContainerView: PictraImageContainerView
    private lateinit var resetButton: Button
    private lateinit var sizeMinusButton: Button
    private lateinit var sizePlusButton: Button
    private lateinit var colorButton: Button
    private lateinit var undoButton: Button
    private lateinit var saveButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_editor)
        pictraImageContainerView = findViewById(R.id.customCanvas)
        resetButton = findViewById(R.id.resetButton)
        sizeMinusButton = findViewById(R.id.sizeMinusButton)
        sizePlusButton = findViewById(R.id.sizePlusButton)
        colorButton = findViewById(R.id.colorButton)
        undoButton = findViewById(R.id.undoButton)
        saveButton = findViewById(R.id.saveButton)
        pictraImageContainerView.setDebugMode(true)
        listeners()

        val url = intent.data;
        if (url != null) {
            val bitmap = decodeUriToBitmap(this, url)
            pictraImageContainerView.setBitmap(bitmap.rotate(90f))
        }
        pictraImageContainerView.setOnUploadListener(this)

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
        saveButton.setOnClickListener {
            val file = getOutputDirectory()
            pictraImageContainerView.saveImageToFile(file)
        }
    }

    private fun resetView() {
        pictraImageContainerView.resetView()
    }

    private fun decodeUriToBitmap(mContext: Context, sendUri: Uri): Bitmap {
        var getBitmap: Bitmap? = null
        try {
            val image_stream: InputStream
            try {
                image_stream = mContext.contentResolver.openInputStream(sendUri)!!
                getBitmap = BitmapFactory.decodeStream(image_stream)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return getBitmap!!
    }

    fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    override fun uploadImage(bitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data: ByteArray = baos.toByteArray()

        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.getReferenceFromUrl("gs://clearquotes-cbf75.appspot.com")
        val fileName = SimpleDateFormat(
            "yyyy-MM-dd-HH-mm-ss-SSS", Locale.US
        ).format(System.currentTimeMillis()) + ".jpg"
        val imagesRef = storageRef.child("images/${fileName}")

        val uploadTask = imagesRef.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            Log.d(TAG, "uploadImage: ${taskSnapshot.storage.downloadUrl}")
            // Do what you want
        }
    }

    companion object {
        private const val TAG = "ImageEditorActivity"
    }
}