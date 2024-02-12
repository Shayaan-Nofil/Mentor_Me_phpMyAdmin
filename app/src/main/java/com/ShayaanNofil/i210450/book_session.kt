package com.ShayaanNofil.i210450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class book_session : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_session)

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val bookbutton=findViewById<View>(R.id.book_button)
        bookbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, booked_sessions::class.java )
            startActivity(temp)
            finish()
        })

        val messagebutton=findViewById<View>(R.id.message_button)
        messagebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, individual_chat::class.java )
            startActivity(temp)
        })

        val callbutton=findViewById<View>(R.id.call_button)
        callbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, voice_call::class.java )
            startActivity(temp)
        })

        val videobutton=findViewById<View>(R.id.video_button)
        videobutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, call_screen_simple::class.java )
            startActivity(temp)
        })
    }
}