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
import com.google.gson.Gson
import org.json.JSONObject

import java.net.URLEncoder

private lateinit var mAuth: FirebaseAuth
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
            Log.w("TAG", "Auto logging in")
            Log.w("TAG", currentUser.uid.toString())
            signinwithmAuth(currentUser.uid.toString())
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

    fun signin(email:String,pass:String){
        var mAuth: FirebaseAuth = Firebase.auth
        mAuth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    FirebaseApp.initializeApp(this)
                    signinsql(email, pass)
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
    private fun signinsql(email: String, pass: String) {
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

                    user.mAuth = Firebase.auth.uid.toString()
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }
                        // Get new FCM registration token
                        val token = task.result
                        Log.d("MyToken", token)
                        user.token = token
                        signup(user.id, token, "user", user.mAuth)

                        Log.w("TAG", "signInWithEmail:success")
                        val secondActivityIntent = Intent(this, home_page::class.java)
                        secondActivityIntent.putExtra("user", user)
                        secondActivityIntent.putExtra("typeofuser", "user")
                        startActivity(secondActivityIntent)
                    })

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

                    user.mAuth = Firebase.auth.uid.toString()
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }
                        // Get new FCM registration token
                        val token = task.result
                        Log.d("MyToken", token)
                        user.token = token
                        signup(user.id, user.token, "mentor", user.mAuth)
                    })

                    val secondActivityIntent = Intent(this, home_page::class.java)
                    val usr = User()
                    usr.name = user.name
                    usr.id = user.id
                    usr.profilepic = user.profilepic
                    usr.mAuth = user.mAuth
                    usr.token = user.token
                    secondActivityIntent.putExtra("user", usr)
                    secondActivityIntent.putExtra("typeofuser", "mentor")
                    startActivity(secondActivityIntent)

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
    private fun signup(userid: String, token: String, table: String, mAuth: String) {
        val serverUrl = server_ip + "updatetoken_user.php"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, serverUrl,
            Response.Listener<String> { response ->
                Log.w("TAG", response)
                finish()
            },
            Response.ErrorListener { error ->
                // Handle error
                Log.w("TAG", "createUserWithEmail:failure")
                val text = "Didn't work"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, text, duration) // in Activity
                toast.show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = userid
                params["token"] = token
                params["table"] = table
                params["mAuth"] = mAuth
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun signinwithmAuth(mAuth: String){
        val serverUrl = server_ip + "Login_usermAuth.php"
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

                    user.mAuth = Firebase.auth.uid.toString()
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }
                        // Get new FCM registration token
                        val token = task.result
                        Log.d("MyToken", token)
                        user.token = token
                        signup(user.id, token, "user", user.mAuth)

                        Log.w("TAG", "signInWithEmail:success")
                        val secondActivityIntent = Intent(this, home_page::class.java)
                        secondActivityIntent.putExtra("user", user)
                        secondActivityIntent.putExtra("typeofuser", "user")
                        startActivity(secondActivityIntent)
                    })

                } else {
                    signinwithmAuthmentor(mAuth)
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
                params["mAuth"] = mAuth
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun signinwithmAuthmentor(mAuth: String){
        val serverUrl = server_ip + "Login_usermAuth.php"
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

                    user.mAuth = Firebase.auth.uid.toString()
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                            return@OnCompleteListener
                        }
                        // Get new FCM registration token
                        val token = task.result
                        Log.d("MyToken", token)
                        user.token = token
                        signup(user.id, user.token, "mentor", user.mAuth)
                    })

                    val secondActivityIntent = Intent(this, home_page::class.java)
                    val usr = User()
                    usr.name = user.name
                    usr.id = user.id
                    usr.profilepic = user.profilepic
                    usr.mAuth = user.mAuth
                    usr.token = user.token
                    secondActivityIntent.putExtra("user", usr)
                    secondActivityIntent.putExtra("typeofuser", "mentor")
                    startActivity(secondActivityIntent)

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
                params["mAuth"] = mAuth
                return params
            }
        }
        requestQueue.add(stringRequest)
    }
}