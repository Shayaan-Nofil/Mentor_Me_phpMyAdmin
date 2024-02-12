package com.ShayaanNofil.i210450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View

class verify_phone : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_phone)

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val verifybutton=findViewById<View>(R.id.verify_button)
        verifybutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, home_page::class.java )
            startActivity(temp)
            finish()
        })

        val againbutton=findViewById<View>(R.id.sendagain_button)
        againbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, verify_phone::class.java )
            startActivity(temp)
            finish()
        })
    }
}