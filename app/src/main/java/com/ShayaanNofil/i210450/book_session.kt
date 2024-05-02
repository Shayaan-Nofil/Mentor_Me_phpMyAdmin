package com.ShayaanNofil.i210450

import Chats
import Mentors
import Sessions
import User
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import java.util.Calendar

private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
class book_session : AppCompatActivity() {
    private var server_ip = "http://192.168.68.184//"
    lateinit var mentor: Mentors
    lateinit var user: User
    private var typeofuser = "user"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_session)
        var session : Sessions = Sessions()
        session.time = "11:00am"

        mentor = intent.getSerializableExtra("object") as Mentors
        user = intent.getSerializableExtra("user") as User
        typeofuser = intent.getStringExtra("typeofuser").toString()

        if (mentor != null){
            val mentorname: TextView = findViewById(R.id.name_text)
            mentorname.text = mentor.name

            val mentorimg: CircleImageView = findViewById(R.id.johnimg)
            Glide.with(this).load(mentor.profilepic).into(mentorimg)

            val ratetext: TextView = findViewById(R.id.review_text)
            val ratestring = "$" + mentor.rate.toString() + "/Session"
            ratetext.text = ratestring

            val ratingtext: TextView = findViewById(R.id.rating_text)
            ratingtext.text = mentor.rating.toString()
        }

        var calendar : CalendarView = findViewById(R.id.calender)
        calendar.setOnDateChangeListener(
            CalendarView.OnDateChangeListener { view, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                Log.w("TAG", "Adding date")
                session.date = dayOfMonth.toString() + "-" + month + "-" + year
                Log.w("TAG", session.date)
            })

        Log.w("TAG", session.date.toString())

        val bt10: Button = findViewById(R.id.bt_10am)
        val bt11: Button = findViewById(R.id.bt_11am)
        val bt12: Button = findViewById(R.id.bt_12pm)

        bt10.setOnClickListener(View.OnClickListener {
            bt10.setBackgroundResource(R.drawable.deepblue_rounded_box)
            bt11.setBackgroundResource(R.drawable.lightgray_rounded_box)
            bt12.setBackgroundResource(R.drawable.lightgray_rounded_box)
            bt10.setTextColor(Color.WHITE)
            bt11.setTextColor(Color.BLACK)
            bt12.setTextColor(Color.BLACK)
            session.time = "10:00am"
        })
        bt11.setOnClickListener(View.OnClickListener {
            bt10.setBackgroundResource(R.drawable.lightgray_rounded_box)
            bt11.setBackgroundResource(R.drawable.deepblue_rounded_box)
            bt12.setBackgroundResource(R.drawable.lightgray_rounded_box)
            bt10.setTextColor(Color.BLACK)
            bt11.setTextColor(Color.WHITE)
            bt12.setTextColor(Color.BLACK)
            session.time = "11:00am"
        })
        bt12.setOnClickListener(View.OnClickListener {
            bt10.setBackgroundResource(R.drawable.lightgray_rounded_box)
            bt11.setBackgroundResource(R.drawable.lightgray_rounded_box)
            bt12.setBackgroundResource(R.drawable.deepblue_rounded_box)
            bt10.setTextColor(Color.BLACK)
            bt11.setTextColor(Color.BLACK)
            bt12.setTextColor(Color.WHITE)
            session.time = "12:00pm"
        })

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val bookbutton=findViewById<View>(R.id.book_button)
        bookbutton.setOnClickListener(View.OnClickListener {
            session.mentorid = mentor.id
            session.mentorimg = mentor.profilepic
            session.mentorname = mentor.name
            session.mentorjob = mentor.job
            session.userid = user.id

            val serverUrl = server_ip + "create_session.php"
            val requestQueue = Volley.newRequestQueue(this)

            val stringRequest = object : StringRequest(
                Method.POST, serverUrl,
                Response.Listener<String> { response ->
                    // Parse the response from the server
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
                    val gson = Gson()
                    val userJson = gson.toJson(session)
                    params["session"] = userJson
                    return params
                }
            }
            requestQueue.add(stringRequest)
        })

        val messagebutton=findViewById<View>(R.id.message_button)
        messagebutton.setOnClickListener(View.OnClickListener {
            checkchat()
        })

        val callbutton=findViewById<View>(R.id.call_button)
        callbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, voice_call::class.java )
            startActivity(temp)
        })

        val videobutton=findViewById<View>(R.id.video_button)
        videobutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, call_screen_simple::class.java )
            startActivity(temp)
        })
    }

    private fun createchat(){
        var chats = Chats()
        chats.mentorid = mentor.id.toInt()
        chats.userid = user.id.toInt()
        chats.mentorname = mentor.name
        chats.mentorimg = mentor.profilepic
        chats.username = user.name
        chats.userimg = user.profilepic

        val serverUrl = server_ip + "create_chat.php"
        val requestQueue = Volley.newRequestQueue(this)

        val stringRequest = object : StringRequest(
            Method.POST, serverUrl,
            Response.Listener<String> { response ->
                checkchat()
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
                val userJson = gson.toJson(chats)
                params["chats"] = userJson
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    private fun checkchat(){
        val serverUrl = server_ip + "check_chat.php"
        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Request.Method.POST, serverUrl,
            { response ->
                Log.d("Server Response", response)

                if (response.trim().startsWith("{")) {
                    val userJson = JSONObject(response)
                    val chat = Chats()
                    chat.id = userJson.getInt("id").toString()
                    chat.userid = userJson.getInt("userid")
                    chat.mentorid = userJson.getInt("mentorid")
                    chat.mentorname = userJson.getString("mentorname")
                    chat.mentorimg = userJson.getString("mentorimg")
                    chat.username = userJson.getString("username")
                    chat.userimg = userJson.getString("userimg")

                    val temp = Intent(this, individual_chat::class.java )
                    temp.putExtra("chat", chat)
                    temp.putExtra("user", user)
                    temp.putExtra("typeofuser", typeofuser)
                    startActivity(temp)
                } else {
                    Log.w("TAG", "No chat found")
                    createchat()
                }
            },
            { error ->
                Log.e("TAG", "Error: ${error.message}", error)
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = user.id
                params["mentorid"] = mentor.id
                return params
            }
        }

        requestQueue.add(stringRequest)
    }
}