package com.ShayaanNofil.i210450

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class voice_call : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_call)

        val endbutton=findViewById<View>(R.id.end_button)
        endbutton.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
}