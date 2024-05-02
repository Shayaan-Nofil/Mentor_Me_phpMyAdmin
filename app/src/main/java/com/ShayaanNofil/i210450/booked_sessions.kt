package com.ShayaanNofil.i210450

import Sessions
import User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import booked_session_recycle_adapter
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray

class booked_sessions : AppCompatActivity() {
    private var server_ip = "http://192.168.68.184//"
    lateinit var usr: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booked_sessions)
        usr = intent.getSerializableExtra("user") as User

        getsessions()


        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })
    }
    private fun getsessions(){
        val recycle_topmentor: RecyclerView = findViewById(R.id.session_recycle)
        recycle_topmentor.layoutManager = LinearLayoutManager(this)
        val sessionarray: MutableList<Sessions> = mutableListOf()


        val serverUrl = server_ip + "get_sessions.php"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Request.Method.POST, serverUrl,
            { response ->
                Log.d("Server Response", response)
                // Parse the JSON response
                val jsonArray = JSONArray(response)

                // Clear the mentorarray
                sessionarray.clear()
                // Loop through the JSON array
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)

                    // Create a new Mentor object
                    val mentor = Sessions()
                    mentor.id = jsonObject.getInt("id").toString()
                    mentor.mentorid = jsonObject.getInt("mentorid").toString()
                    mentor.mentorname = jsonObject.getString("mentorname")
                    mentor.userid = jsonObject.getInt("userid").toString()
                    mentor.mentorjob = jsonObject.getString("mentorjob")
                    mentor.mentorimg = jsonObject.getString("mentorimg")
                    mentor.date = jsonObject.getString("date")
                    mentor.time = jsonObject.getString("time")

                    sessionarray.add(mentor)
                }

                val adapter = booked_session_recycle_adapter(sessionarray)
                recycle_topmentor.adapter = adapter
            },
            { error ->
                Log.e("TAG", "Error: ${error.message}", error)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = usr.id
                return params
            }
        }

        requestQueue.add(stringRequest)
    }
}
