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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Response
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
import org.json.JSONObject

import java.net.URLEncoder

private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
class Log_in_page : AppCompatActivity() {

    lateinit var email: EditText
    lateinit var password: EditText
    private var server_ip = "http://192.168.18.70//"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        mAuth = Firebase.auth
        // Check if user is already logged in
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
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
            // User is already logged in, navigate to home page
            val secondActivityIntent = Intent(this, home_page::class.java)
            startActivity(secondActivityIntent)
            finish()
            return
        }


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

    private fun signin(email: String, pass: String) {
        val serverUrl = server_ip + "Login_user.php"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, serverUrl,
            Response.Listener<String> { response ->
                // Parse the response from the server
                if (response != "bad") {
                    // Parse the JSON response into a User object
                    val userJson = JSONObject(response)
                    val user = User()
                    user.id = userJson.getString("id")
                    user.name = userJson.getString("name")
                    user.email = userJson.getString("email")
                    user.number = userJson.getString("number")
                    user.city = userJson.getString("city")
                    user.country = userJson.getString("country")
                    user.token = userJson.getString("token")
                    user.profilepic = userJson.getString("profilepic")
                    user.bgpic = userJson.getString("bgpic")

                    val secondActivityIntent = Intent(this, home_page::class.java)
                    secondActivityIntent.putExtra("user", user)
                    startActivity(secondActivityIntent)
                    finish()
                } else {
                    signinmentor(email, pass)
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                Log.w("TAG", "signInWithEmail:failure", error)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["table"] = "user"
                params["email"] = email
                params["password"] = pass
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun signinmentor(email: String, pass: String) {
        val serverUrl = server_ip + "Login_user.php"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, serverUrl,
            Response.Listener<String> { response ->
                // Parse the response from the server
                if (response != "bad") {
                    // Parse the JSON response into a User object
                    val userJson = JSONObject(response)
                    val user = Mentors()
                    user.id = userJson.getString("id")
                    user.name = userJson.getString("name")
                    user.email = userJson.getString("email")
                    user.token = userJson.getString("token")
                    user.profilepic = userJson.getString("profilepic")
                    user.job = userJson.getString("job")
                    user.description = userJson.getString("description")
                    user.rate = userJson.getInt("rate")
                    user.rating = userJson.getDouble("rating")
                    user.status = userJson.getString("status")

                    val secondActivityIntent = Intent(this, home_page::class.java)
                    secondActivityIntent.putExtra("user", user)
                    startActivity(secondActivityIntent)
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    val text = "Wrong Credentials"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this, text, duration)
                    toast.show()
                }
            },
            Response.ErrorListener { error ->
                // Handle error
                Log.w("TAG", "signInWithEmail:failure", error)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["table"] = "mentor"
                params["email"] = email
                params["password"] = pass
                return params
            }
        }
        requestQueue.add(stringRequest)
    }
//    fun signin(email:String,pass:String){
//        var mAuth: FirebaseAuth = Firebase.auth
//        mAuth.signInWithEmailAndPassword(email, pass)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    FirebaseApp.initializeApp(this)
//                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//                        if (!task.isSuccessful) {
//                            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
//                            return@OnCompleteListener
//                        }
//                        // Get new FCM registration token
//                        val token = task.result
//                        Log.d("MyToken", token)
//
//                        FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).addListenerForSingleValueEvent(object:
//                            ValueEventListener {
//                            override fun onDataChange(snapshot: DataSnapshot) {
//                                if (snapshot.exists()) {
//                                    Log.w("TAG", "User is a user")
//                                    var usr : User = snapshot.getValue(User::class.java)!!
//                                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//                                        if (!task.isSuccessful) {
//                                            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
//                                            return@OnCompleteListener
//                                        }
//                                        // Get new FCM registration token
//                                        val token = task.result
//                                        Log.d("MyToken", token)
//                                        usr.token = token
//                                        FirebaseDatabase.getInstance().getReference("User")
//                                            .child(Firebase.auth.uid.toString())
//                                            .child("token")
//                                            .setValue(token)
//                                    })
//
//                                }
//                                else{
//                                    Log.w("TAG", "User is a mentor")
//                                    FirebaseDatabase.getInstance().getReference("Mentor").child(mAuth.uid.toString()).addListenerForSingleValueEvent(object:
//                                        ValueEventListener {
//                                        override fun onDataChange(snapshot: DataSnapshot) {
//                                            if (snapshot.exists()) {
//                                                var usr : Mentors = snapshot.getValue(Mentors::class.java)!!
//                                                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//                                                    if (!task.isSuccessful) {
//                                                        Log.w("TAG", "Fetching FCM registration token failed", task.exception)
//                                                        return@OnCompleteListener
//                                                    }
//                                                    // Get new FCM registration token
//                                                    val token = task.result
//                                                    Log.d("MyToken", token)
//                                                    usr.token = token
//                                                    FirebaseDatabase.getInstance().getReference("Mentor")
//                                                        .child(Firebase.auth.uid.toString())
//                                                        .child("token")
//                                                        .setValue(token)
//                                                })
//                                            }
//                                        }
//                                        override fun onCancelled(error: DatabaseError) {}
//                                    })
//
//                                }
//                            }
//                            override fun onCancelled(error: DatabaseError) {}
//                        })
//                    })
//
//                    var secondActivityIntent = Intent(this, home_page::class.java)
//                    startActivity(secondActivityIntent)
//                    finish()
//                }
//                else {
//                    // If sign in fails, display a message to the user.
//                    Log.w("TAG", "signInWithEmail:failure", task.exception)
//                    val text = "Wrong Credentials"
//                    val duration = Toast.LENGTH_SHORT
//                    val toast = Toast.makeText(this, text, duration) // in Activity
//                    toast.show()
//                }
//            }
//        }
    }