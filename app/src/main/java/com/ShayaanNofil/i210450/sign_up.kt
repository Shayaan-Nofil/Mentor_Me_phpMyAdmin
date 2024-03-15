package com.ShayaanNofil.i210450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

private lateinit var mAuth: FirebaseAuth
class sign_up : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        mAuth = Firebase.auth
        val loginbutton=findViewById<View>(R.id.login)
        loginbutton.setOnClickListener(View.OnClickListener {
            val login = Intent(this, Log_in_page::class.java )
            startActivity(login)
            finish()
        })

        val signupbutton=findViewById<View>(R.id.signup_button)
        signupbutton.setOnClickListener(View.OnClickListener {
            var email: EditText
            var password: EditText
            email = findViewById<EditText>(R.id.email_box)
            password = findViewById<EditText>(R.id.password_box)

            if (email.text.isEmpty()){
                email.setText(" ")
            }
            if (password.text.isEmpty()){
                password.setText(" ")
            }

            signup(email.getText().toString(), password.getText().toString())
        })
    }
    fun signup(email:String,pass:String){
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "createUserWithEmail:success")

                    val user = mAuth.currentUser
                    var secondActivityIntent = Intent(this, verify_phone::class.java)
                    startActivity(secondActivityIntent)
                } else {
// If sign in fails, display a message to the user.
                    Log.w("TAG", "createUserWithEmail:failure", task.exception)
                    val text = "Didnt work"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this, text, duration) // in Activity
                    toast.show()
                }
            }
    }
}