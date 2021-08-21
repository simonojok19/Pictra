package com.example.pictra

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class TextInputActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_text_input)

        val addButton: Button = findViewById(R.id.add_button)
        addButton.setOnClickListener {
            val input: TextView = findViewById(R.id.text_input)
            val text = input.text.toString()
            if (text.isNotEmpty()) {
                val intent = Intent()
                intent.putExtra(TEXT_ENTERED, text)
                setResult(RESULT_OK, intent)
                finish()
            } else {
                Toast.makeText(this, "Please enter some value", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    companion object {
        const val TEXT_ENTERED = "com.example.pictra.TextInputActivity,TEXT_ENTERED"
    }
}