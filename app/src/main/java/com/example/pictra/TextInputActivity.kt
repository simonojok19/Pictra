package com.example.pictra

import android.app.Activity
import android.os.Bundle

class TextInputActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_input)
    }

    companion object {
        const val TEXT_ENTERED = "com.example.pictra.TextInputActivity,TEXT_ENTERED"
    }
}