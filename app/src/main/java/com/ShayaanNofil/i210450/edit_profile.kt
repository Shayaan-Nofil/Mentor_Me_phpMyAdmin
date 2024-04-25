package com.ShayaanNofil.i210450

import User
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView


private lateinit var database: DatabaseReference
private lateinit var mAuth: FirebaseAuth

class edit_profile : AppCompatActivity() {
    private var profilePictureUri: Uri? = null
    lateinit var profilepicimg: CircleImageView
    lateinit var usr: User
    lateinit var namebox: EditText
    lateinit var emailbox: EditText
    lateinit var countrybox: Spinner
    lateinit var citybox: EditText
    lateinit var numbox: EditText
    private var server_ip = "http://192.168.18.70//"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)


        profilepicimg = findViewById(R.id.profile_pic)
        namebox = findViewById(R.id.name_box)
        emailbox = findViewById(R.id.email_box)
        citybox = findViewById(R.id.city_box)
        numbox = findViewById(R.id.contact_box)
        usr = intent.getSerializableExtra("user") as User
        namebox.hint = usr.name
        emailbox.hint = usr.email
        citybox.hint = usr.city
        numbox.hint = usr.number

        Glide.with(this)
            .load(usr.profilepic)
            .into(profilepicimg)


        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        namebox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                usr.name = namebox.text.toString()
            }
        })

        numbox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                usr.number = numbox.text.toString()
            }
        })

        citybox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                usr.city = citybox.text.toString()
            }
        })


        val updatebutton=findViewById<View>(R.id.update_button)
        updatebutton.setOnClickListener(View.OnClickListener {
            updateuser(usr)
        })
    }
    private fun updateuser(user: User) {
        val serverUrl = server_ip + "update_user.php"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, serverUrl,
            Response.Listener<String> { response ->
                // Parse the response from the server
                val secondActivityIntent = Intent(this, profile_page::class.java)
                secondActivityIntent.putExtra("user", user)
                startActivity(secondActivityIntent)
                finish()
            },
            Response.ErrorListener { error ->
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

}