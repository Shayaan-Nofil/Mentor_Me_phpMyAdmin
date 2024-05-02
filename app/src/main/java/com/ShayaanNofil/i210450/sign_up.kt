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
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.Response
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import org.json.JSONObject


private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
class sign_up : AppCompatActivity() {
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var name: EditText
    lateinit var number: EditText
    lateinit var city: EditText
    lateinit var  country: Spinner
    private var server_ip = "http://192.168.68.184//"
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
                val user = User()
                user.name = name.text.toString()
                user.email = email.text.toString()
                user.password = password.text.toString()
                user.number = number.text.toString()
                user.city = city.text.toString()
                user.country = country.selectedItem.toString()

                signup(user.email, user.password, user)
            }

        })
    }

    private fun signupsql(user: User) {
        val serverUrl = server_ip + "Signup_user.php"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, serverUrl,
            Response.Listener<String> { response ->
                // Parse the response from the server
                user.mAuth = Firebase.auth.uid.toString()
                signinsql(user.email, user.password)
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
                val gson = Gson()
                val userJson = gson.toJson(user)
                params["user"] = userJson
                return params
            }
        }
        requestQueue.add(stringRequest)
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
                        signuptoken(user.id, token, "user", user.mAuth)

                        Log.w("TAG", "signInWithEmail:success")
                        val secondActivityIntent = Intent(this, profile_page::class.java)
                        secondActivityIntent.putExtra("user", user)
                        startActivity(secondActivityIntent)
                    })

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

    fun signup(email:String,pass:String, user: User){
        mAuth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TAG", "createUserWithEmail:success")
                    signupsql(user)
                }
        }
    }

    private fun signuptoken(userid: String, token: String, table: String, mAuth: String) {
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
}