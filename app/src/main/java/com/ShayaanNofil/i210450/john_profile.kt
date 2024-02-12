package com.ShayaanNofil.i210450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class john_profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_john_profile)

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val revbutton=findViewById<View>(R.id.bt_drop_rev)
        revbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, drop_review::class.java )
            startActivity(temp)
        })

        val combutton=findViewById<View>(R.id.bt_join_com)
        combutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, community_chat::class.java )
            startActivity(temp)
        })
    }
}