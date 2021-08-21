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
import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageActivity
import com.theartofdev.edmodo.cropper.CropImageView
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
    private lateinit var addText: Button
    private lateinit var cropButton: Button
    private lateinit var cropImageLauncher: ActivityResultLauncher<Intent>


    private lateinit var textInputLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_editor)
        pictraImageContainerView = findViewById(R.id.customCanvas)
        resetButton = findViewById(R.id.resetButton)
        sizeMinusButton = findViewById(R.id.sizeMinusButton)
        sizePlusButton = findViewById(R.id.sizePlusButton)
        colorButton = findViewById(R.id.colorButton)
        undoButton = findViewById(R.id.undoButton)
        addText = findViewById(R.id.addText)
        cropButton = findViewById(R.id.crop)

        pictraImageContainerView.setDebugMode(true)
        listeners()

        val url = intent.data;
        if (url != null) {
            val bitmap = decodeUriToBitmap(this, url)
            pictraImageContainerView.setBitmap(bitmap.rotate(90f))
        }
        pictraImageContainerView.setOnUploadListener(this)

        textInputLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data
            if (data != null) {
                val text = data.getStringExtra(TextInputActivity.TEXT_ENTERED)
                pictraImageContainerView.drawUserText(text ?: "")
            }

        }

        cropImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data = it.data
            if (data != null) {
                val uri = data.data!!
                val bitmap = decodeUriToBitmap(this, uri)
                pictraImageContainerView.setBitmap(bitmap)
            }
        }

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

        addText.setOnClickListener {
            textInputLauncher.launch(Intent(this, TextInputActivity::class.java))
        }


        cropButton.setOnClickListener {
            CropImage.activity(intent.data!!)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setScaleType(
                    CropImageView.ScaleType.CENTER_INSIDE
                )
                .start(this);
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
        val progressDoalog = ProgressDialog(this)
        progressDoalog.setMessage("Uploading Image Please await ....");
        progressDoalog.setTitle("Upload Image");
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.show();
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
            progressDoalog.hide()
            Toast.makeText(this, "Error Occurred Uploading Image", Toast.LENGTH_SHORT).show()
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            Log.d(TAG, "uploadImage: ${taskSnapshot.storage.downloadUrl}")
            progressDoalog.hide()
            Toast.makeText(this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show()
            finish()
            // Do what you want
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.image_edit_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.upload) {
            val file = getOutputDirectory()
            pictraImageContainerView.saveImageToFile(file)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                val resultUri = result.uri
                if (result != null) {
                    val bitmap = decodeUriToBitmap(this, resultUri)
                    pictraImageContainerView.setBitmap(bitmap)
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }
    }

    companion object {
        private const val TAG = "ImageEditorActivity"
    }
}