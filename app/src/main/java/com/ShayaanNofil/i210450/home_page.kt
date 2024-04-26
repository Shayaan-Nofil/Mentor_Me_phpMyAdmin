package com.ShayaanNofil.i210450

import Mentors
import User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONArray
import org.json.JSONObject

private lateinit var database: DatabaseReference
private lateinit var mAuth: FirebaseAuth
class home_page : AppCompatActivity() {
    private var server_ip = "http://192.168.18.70//"
    private var user = User()
    private var typeofuser = "user"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        user = intent.getSerializableExtra("user") as User
        typeofuser = intent.getStringExtra("typeofuser").toString()

        val nametext: TextView = findViewById(R.id.user_name_text)
        nametext.text = user.name

        val recycle_topmentor: RecyclerView = findViewById(R.id.recycle_top_mentors)
        val recycle_education: RecyclerView = findViewById(R.id.recycle_education)
        val recycle_recent: RecyclerView = findViewById(R.id.recycle_recent)
        recycle_topmentor.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        recycle_education.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        recycle_recent.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        var mentorarray: MutableList<Mentors> = mutableListOf()

        val serverUrl = server_ip + "getall_mentors.php"

        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = StringRequest(
            Request.Method.GET, serverUrl,
            { response ->
                // Parse the JSON response
                val jsonArray = JSONArray(response)

                // Clear the mentorarray
                mentorarray.clear()

                // Loop through the JSON array
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    // Create a new Mentor object
                    val mentor = Mentors()
                    mentor.id = jsonObject.getString("id")
                    mentor.name = jsonObject.getString("name")
                    mentor.email = jsonObject.getString("email")
                    mentor.job = jsonObject.getString("job")
                    mentor.description = jsonObject.getString("description")
                    mentor.rating = jsonObject.getDouble("rating")
                    mentor.profilepic = jsonObject.getString("profilepic")
                    mentor.rate = jsonObject.getInt("rate")
                    mentor.status = jsonObject.getString("status")

                    Log.w("TAG", mentor.name)
                    mentorarray.add(mentor)
                }

                val adapter = homerecycle_adapter(mentorarray)
                recycle_topmentor.adapter = adapter
                recycle_education.adapter = adapter
                recycle_recent.adapter = adapter

                adapter.setOnClickListener(object :
                    homerecycle_adapter.OnClickListener {
                    override fun onClick(position: Int, model: Mentors) {
                        val intent = Intent(this@home_page, john_profile::class.java)
                        intent.putExtra("object", model)
                        intent.putExtra("user", user)
                        startActivity(intent)
                    }
                })
            },
            { error ->
                Log.e("TAG", "Error: ${error.message}", error)
            }
        )
        requestQueue.add(stringRequest)



        val notifbutton=findViewById<View>(R.id.notif_button)
        notifbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, notifications::class.java )
            startActivity(temp)
        })

        val task_homebutton=findViewById<View>(R.id.bthome)
        task_homebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, home_page::class.java )
            temp.putExtra("user", user)
            temp.putExtra("typeofuser", typeofuser)
            startActivity(temp)
        })

        val task_searchbutton=findViewById<View>(R.id.btsearch)
        task_searchbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, Search::class.java )
            temp.putExtra("user", user)
            temp.putExtra("typeofuser", typeofuser)
            startActivity(temp)
        })

        val task_chatbutton=findViewById<View>(R.id.btchat)
        task_chatbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, chats_page::class.java )
            temp.putExtra("user", user)
            temp.putExtra("typeofuser", typeofuser)
            startActivity(temp)
        })

        val task_profilebutton=findViewById<View>(R.id.btprofile)
        task_profilebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, profile_page::class.java )
            temp.putExtra("user", user)
            temp.putExtra("typeofuser", typeofuser)
            startActivity(temp)
        })

        val task_addcontent=findViewById<View>(R.id.btaddcontent)
        task_addcontent.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, addnew_mentor::class.java )
            temp.putExtra("user", user)
            temp.putExtra("typeofuser", typeofuser)
            startActivity(temp)
        })
    }

    private fun getdata(usr: User){
        val url = server_ip + "getuserdata.php"

        val params = HashMap<String, String>()
        params["userId"] = usr.id

        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url,
            Response.Listener<String> { response ->
                // Parse the JSON response
                val userJson = JSONObject(response)
                // Get the user data from the JSON object
                usr.id = userJson.getString("id")
                usr.name = userJson.getString("name")
                usr.email = userJson.getString("email")
                usr.number = userJson.getString("number")
                usr.city = userJson.getString("city")
                usr.country = userJson.getString("country")
                usr.token = userJson.getString("token")
                usr.profilepic = userJson.getString("profilepic")
                usr.bgpic = userJson.getString("bgpic")
            },
            Response.ErrorListener { error ->
            }
        ) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }
    }

}
