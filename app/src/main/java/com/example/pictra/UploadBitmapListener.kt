package com.example.pictra

import android.graphics.Bitmap

interface UploadBitmapListener {
    fun uploadImage(bitmap: Bitmap)
}