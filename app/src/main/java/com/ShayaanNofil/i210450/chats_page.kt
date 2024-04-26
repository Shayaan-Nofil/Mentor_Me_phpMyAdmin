package com.ShayaanNofil.i210450

import Chats
import Mentors
import User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chatsearch_recycle_adapter
import com.android.volley.Request
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
import com.google.firebase.database.getValue
import com.google.gson.JsonObject
import org.json.JSONArray

private lateinit var mAuth: FirebaseAuth
private lateinit var typeofuser: String
private lateinit var userchats : MutableList<String>
class chats_page : AppCompatActivity() {
    lateinit var user: User
    private var server_ip = "http://192.168.18.70//"
    private var typeofuser = "user"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats_page)

        user = intent.getSerializableExtra("user") as User
        typeofuser = intent.getStringExtra("typeofuser").toString()

        getchats()

        val backbutton = findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val task_homebutton = findViewById<View>(R.id.bthome)
        task_homebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, home_page::class.java)
            temp.putExtra("user", user)
            startActivity(temp)
        })

        val task_searchbutton = findViewById<View>(R.id.btsearch)
        task_searchbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, Search::class.java)
            temp.putExtra("user", user)
            intent.putExtra("typeofuser", typeofuser)
            startActivity(temp)
        })

        val task_chatbutton = findViewById<View>(R.id.btchat)
        task_chatbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, chats_page::class.java)
            temp.putExtra("user", user)
            startActivity(temp)
        })

        val task_profilebutton = findViewById<View>(R.id.btprofile)
        task_profilebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, profile_page::class.java)
            temp.putExtra("user", user)
            startActivity(temp)
        })

        val task_addcontent = findViewById<View>(R.id.btaddcontent)
        task_addcontent.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, addnew_mentor::class.java)
            temp.putExtra("user", user)
            startActivity(temp)
        })
    }

    private fun getchats(){
        val chatarray: MutableList<Chats> = mutableListOf()

        val serverUrl = server_ip + "get_chats.php"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Request.Method.POST, serverUrl,
            { response ->
                Log.d("Server Response", response)
                // Parse the JSON response
                val jsonArray = JSONArray(response)

                // Clear the mentorarray
                chatarray.clear()
                // Loop through the JSON array
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    // Create a new Mentor object
                    val mentor = Chats()
                    mentor.id = jsonObject.getInt("id").toString()
                    mentor.mentorid = jsonObject.getInt("mentorid")
                    mentor.userid = jsonObject.getInt("userid")
                    mentor.messagecount = jsonObject.getInt("messagecount")
                    mentor.mentorname = jsonObject.getString("mentorname")
                    mentor.mentorimg = jsonObject.getString("mentorimg")
                    mentor.username = jsonObject.getString("username")
                    mentor.userimg = jsonObject.getString("userimg")


                    chatarray.add(mentor)
                }

                val recycle_chat: RecyclerView = findViewById(R.id.chatpage_recycler_view)
                recycle_chat.layoutManager = LinearLayoutManager(this@chats_page)
                val adapter = chatsearch_recycle_adapter(chatarray, user, typeofuser)
                recycle_chat.adapter = adapter

                adapter.setOnClickListener(object :
                    chatsearch_recycle_adapter.OnClickListener {
                    override fun onClick(position: Int, model: Chats) {
                        val intent = Intent(this@chats_page, individual_chat::class.java)
                        intent.putExtra("object", model)
                        intent.putExtra("user", user)
                        intent.putExtra("typeofuser", typeofuser)
                        startActivity(intent)
                    }
                })
            },
            { error ->
                Log.e("TAG", "Error: ${error.message}", error)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["column"] = "userid"
                if (typeofuser == "mentor"){
                    params["column"] = "mentorid"
                }
                params["userid"] = user.id
                return params
            }
        }

        requestQueue.add(stringRequest)
    }

}
