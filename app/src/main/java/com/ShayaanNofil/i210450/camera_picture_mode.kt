package com.ShayaanNofil.i210450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class camera_picture_mode : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_picture_mode)

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val videobutton=findViewById<View>(R.id.video_button)
        videobutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, camera_video_mode::class.java )
            startActivity(temp)
        })
    }
}