package com.ShayaanNofil.i210450

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.logging.Handler

class main_splash_screen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_splash_screen)

        FirebaseApp.initializeApp(this)

        CoroutineScope(Dispatchers.Main).launch {
            // Delay for 5 seconds
            delay(1000)

            // Open the new activity here
            val intent = Intent(this@main_splash_screen, Log_in_page::class.java)
            startActivity(intent)

            // Finish the current activity if needed
            finish()

        }
    }

}