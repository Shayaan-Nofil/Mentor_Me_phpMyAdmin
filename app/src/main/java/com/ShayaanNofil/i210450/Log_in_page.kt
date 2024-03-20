package com.ShayaanNofil.i210450

import Mentors
import User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
class Log_in_page : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var password: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)


        val loginbutton=findViewById<View>(R.id.login_button)
        loginbutton.setOnClickListener(View.OnClickListener {
            email = findViewById<EditText>(R.id.email_box)
            password = findViewById<EditText>(R.id.password_box)

            if (email.text.toString() == ""){
                email.setText(" ")
            }
            if (password.text.toString() == ""){
                password.setText(" ")
            }

            signin(email.text.toString(), password.text.toString())
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
        var mAuth: FirebaseAuth = Firebase.auth
        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
//                    Log.d("TAG", "signInWithEmail:success")
//                    var mentor: Mentors = Mentors()
//                    mentor.name = "John Cooper"
//                    mentor.email = email
//                    mentor.password = pass
//                    mentor.job = "UX Designer at\nGoogle"
//                    mentor.description = "I am a passionate UX designer at Google with a focus on\n" +
//                            "creating user-centric and intuitive interfaces. With 10 years of\n" +
//                            "experience, I have had the opportunity to work on diverse\n" +
//                            "projects that have shaped my understanding of design\n" +
//                            "principles and user experience"
//                    mentor.rating = 4.8
//                    mentor.id = mAuth.uid!!
//                    mentor.profilepic = "https://firebasestorage.googleapis.com/v0/b/smd-assignment-5b0a4.appspot.com/o/john1.png?alt=media&token=73e33f4e-89be-4071-b6ad-88e5cb3d8a7b"
//                    mentor.rate = 1500
//
//                    val userid = mAuth.uid
//                    database = FirebaseDatabase.getInstance().getReference("Mentor")
//
//                    database.child(userid!!).setValue(mentor).addOnCompleteListener {
//                        var secondActivityIntent = Intent(this, profile_page::class.java)
//                        startActivity(secondActivityIntent)
//                        finish()
//                    }.addOnFailureListener{
//                        Log.w("TAG", "Didnt Register", task.exception)
//                    }

                    FirebaseApp.initializeApp(this)
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }
                        // Get new FCM registration token
                        val token = task.result
                        Log.d("MyToken", token)

                        FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).addListenerForSingleValueEvent(object:
                            ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    Log.w("TAG", "User is a user")
                                    var usr : User = snapshot.getValue(User::class.java)!!
                                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                        if (!task.isSuccessful) {
                                            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                                            return@OnCompleteListener
                                        }
                                        // Get new FCM registration token
                                        val token = task.result
                                        Log.d("MyToken", token)
                                        usr.token = token
                                        FirebaseDatabase.getInstance().getReference("User")
                                            .child(Firebase.auth.uid.toString())
                                            .child("token")
                                            .setValue(token)
                                    })

                                }
                                else{
                                    Log.w("TAG", "User is a mentor")
                                    FirebaseDatabase.getInstance().getReference("Mentor").child(mAuth.uid.toString()).addListenerForSingleValueEvent(object:
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            if (snapshot.exists()) {
                                                var usr : Mentors = snapshot.getValue(Mentors::class.java)!!
                                                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                                    if (!task.isSuccessful) {
                                                        Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                                                        return@OnCompleteListener
                                                    }
                                                    // Get new FCM registration token
                                                    val token = task.result
                                                    Log.d("MyToken", token)
                                                    usr.token = token
                                                    FirebaseDatabase.getInstance().getReference("Mentor")
                                                        .child(Firebase.auth.uid.toString())
                                                        .child("token")
                                                        .setValue(token)
                                                })
                                            }
                                        }
                                        override fun onCancelled(error: DatabaseError) {}
                                    })

                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    })

                    var secondActivityIntent = Intent(this, home_page::class.java)
                    startActivity(secondActivityIntent)
                    finish()
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