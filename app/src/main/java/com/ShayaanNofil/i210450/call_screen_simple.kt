package com.ShayaanNofil.i210450

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class call_screen_simple : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call_screen_simple)

        val backbutton=findViewById<View>(R.id.end_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
}