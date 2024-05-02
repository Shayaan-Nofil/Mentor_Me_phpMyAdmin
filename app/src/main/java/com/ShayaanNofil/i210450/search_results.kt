package com.ShayaanNofil.i210450

import Mentors
import User
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONArray
import org.json.JSONObject
import searchrecycle_adapter

class search_results : AppCompatActivity() {
    private var server_ip = "http://192.168.68.184//"
    private lateinit var mentorname: String
    private lateinit var usr: User
    private var typeofuser = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        val intent = intent
        mentorname = intent.getStringExtra("mentor").toString()
        usr = intent.getSerializableExtra("user") as User
        typeofuser = intent.getStringExtra("typeofuser").toString()

        if (mentorname.isEmpty()){
            getallmentors()
        }
        else{
            getspecificmentors()
        }

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })


        val task_homebutton=findViewById<View>(R.id.bthome)
        task_homebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, home_page::class.java )
            startActivity(temp)
        })

        val task_searchbutton=findViewById<View>(R.id.btsearch)
        task_searchbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, Search::class.java )
            startActivity(temp)
        })

        val task_chatbutton=findViewById<View>(R.id.btchat)
        task_chatbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, chats_page::class.java )
            startActivity(temp)
        })

        val task_profilebutton=findViewById<View>(R.id.btprofile)
        task_profilebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, profile_page::class.java )
            startActivity(temp)
        })

        val task_addcontent=findViewById<View>(R.id.btaddcontent)
        task_addcontent.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, addnew_mentor::class.java )
            startActivity(temp)
        })
    }

    private fun getallmentors(){
        val recycle_topmentor: RecyclerView = findViewById(R.id.searchresults_recycle)
        recycle_topmentor.layoutManager = LinearLayoutManager(this)

        val mentorarray: MutableList<Mentors> = mutableListOf()
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

                adapter.setOnClickListener(object :
                    homerecycle_adapter.OnClickListener {
                    override fun onClick(position: Int, model: Mentors) {
                        val intent = Intent(this@search_results, book_session::class.java)
                        intent.putExtra("object", model)
                        intent.putExtra("user", usr)
                        intent.putExtra("typeofuser", typeofuser)
                        startActivity(intent)
                    }
                })
            },
            { error ->
                Log.e("TAG", "Error: ${error.message}", error)
            }
        )
        requestQueue.add(stringRequest)
    }

    private fun getspecificmentors(){
        val recycle_topmentor: RecyclerView = findViewById(R.id.searchresults_recycle)
        recycle_topmentor.layoutManager = LinearLayoutManager(this)

        val mentorarray: MutableList<Mentors> = mutableListOf()
        val serverUrl = server_ip + "getspecific_mentors.php"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            serverUrl,
            Response.Listener<String> { response ->
                // Parse the JSON response
                val jsonArray = JSONArray(response)

                // Clear the mentorarray
                mentorarray.clear()
                Log.w("TAG", jsonArray.toString())
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

                    mentorarray.add(mentor)
                }

                val adapter = homerecycle_adapter(mentorarray)
                recycle_topmentor.adapter = adapter

                adapter.setOnClickListener(object :
                    homerecycle_adapter.OnClickListener {
                    override fun onClick(position: Int, model: Mentors) {
                        val intent = Intent(this@search_results, book_session::class.java)
                        intent.putExtra("object", model)
                        intent.putExtra("user", usr)
                        startActivity(intent)
                    }
                })
            },
            Response.ErrorListener { error ->
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["mentorname"] = mentorname
                return params
            }
        }
        requestQueue.add(stringRequest)
    }
}