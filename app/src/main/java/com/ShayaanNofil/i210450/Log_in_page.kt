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

class Log_in_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        mAuth = Firebase.auth
        val loginbutton=findViewById<View>(R.id.login_button)

        loginbutton.setOnClickListener(View.OnClickListener {
            var email:EditText
            var password:EditText
            email = findViewById<EditText>(R.id.email_box)
            password = findViewById<EditText>(R.id.password_box)

            if (email.text.toString() == ""){
                email.setText(" ")
            }
            if (password.text.toString() == ""){
                password.setText(" ")
            }

            signin(email.getText().toString(), password.getText().toString())
        })

        val signupbutton=findViewById<View>(R.id.signup)
        signupbutton.setOnClickListener(View.OnClickListener {
            val signin = Intent(this, sign_up::class.java )
            startActivity(signin)
        })

        val newpassword=findViewById<View>(R.id.forgot_password)
        newpassword.setOnClickListener(View.OnClickListener {
            val changepassword = Intent(this, forgot_password_email::class.java )
            startActivity(changepassword)
        })
    }
    fun signin(email:String,pass:String){
        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success")
                    val user = mAuth.currentUser
                    var secondActivityIntent = Intent(this, home_page::class.java)
                    startActivity(secondActivityIntent)
                    //finish()
                }
                else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    val text = "Wrong Credentials"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this, text, duration) // in Activity
                    toast.show()
                }
            }
        }
    }