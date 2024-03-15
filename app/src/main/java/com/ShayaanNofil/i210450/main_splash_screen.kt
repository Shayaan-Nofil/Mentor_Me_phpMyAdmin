package com.ShayaanNofil.i210450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler

class main_splash_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_splash_screen)

        CoroutineScope(Dispatchers.Main).launch {
            // Delay for 5 seconds
            delay(5000)

            // Open the new activity here
            val intent = Intent(this@main_splash_screen, Log_in_page::class.java)
            startActivity(intent)

            // Finish the current activity if needed
            finish()

        }
    }
}