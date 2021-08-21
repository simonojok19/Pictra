package com.example.pictra

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.theartofdev.edmodo.cropper.CropImageView

class ImageCropperActivity : AppCompatActivity() {
    private lateinit var cropImageView: CropImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_cropper)

        cropImageView = findViewById(R.id.cropImageView)

        cropImageView.setImageUriAsync(intent.data);

    }

    companion object {
        const val IMAGE_URL = "com.example.pictra,ImageCropperActivity.IMAGE_URL"
    }
}