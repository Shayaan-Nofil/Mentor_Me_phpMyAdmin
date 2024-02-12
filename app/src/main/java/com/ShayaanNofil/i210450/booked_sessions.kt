package com.ShayaanNofil.i210450

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class booked_sessions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booked_sessions)

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
}