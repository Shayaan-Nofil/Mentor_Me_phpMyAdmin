package com.ShayaanNofil.i210450

import User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
class sign_up : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var name: EditText
    lateinit var number: EditText
    lateinit var city: EditText
    lateinit var  country: Spinner
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

            name = findViewById(R.id.name_box)
            email = findViewById(R.id.email_box)
            password = findViewById(R.id.password_box)
            number = findViewById(R.id.contact_box)
            city = findViewById(R.id.city_box)
            country = findViewById(R.id.country_box)

            if (email.text.isNotEmpty() && password.text.isNotEmpty() && name.text.isNotEmpty() && number.text.isNotEmpty() && city.text.isNotEmpty() && country.selectedItem.toString().isNotEmpty()){
                signup(email.text.toString(), password.text.toString())
            }

        })
    }
    fun signup(email:String,pass:String){
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "createUserWithEmail:success")

                    database = FirebaseDatabase.getInstance().getReference("User")
                    var usr: User = User()
                    var userId = mAuth.uid;
                    usr.addData(userId.toString(), name.text.toString(), email, number.text.toString(), city.text.toString(), country.selectedItem.toString())


                    database.child(userId!!).setValue(usr).addOnCompleteListener {
                        var secondActivityIntent = Intent(this, profile_page::class.java)
                        startActivity(secondActivityIntent)
                        finish()
                    }.addOnFailureListener{
                        Log.w("TAG", "Didnt Register", task.exception)
                    }

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