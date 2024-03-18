package com.ShayaanNofil.i210450

import Chats
import Mentors
import Sessions
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Calendar

private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
class book_session : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_session)
        var session : Sessions = Sessions()
        session.time = "11:00am"

        val mentor: Mentors = intent.getSerializableExtra("object") as Mentors

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
            mAuth = Firebase.auth
            session.userid = mAuth.uid!!

            database = FirebaseDatabase.getInstance().getReference("Session")
            session.id = database.push().key.toString()

            database.child(session.id).setValue(session).addOnCompleteListener {
                Log.w("TAG", "Review uploaded")
                finish()
            }.addOnFailureListener{
                Log.w("TAG", "Didnt upload Review")
            }
        })

        val messagebutton=findViewById<View>(R.id.message_button)
        messagebutton.setOnClickListener(View.OnClickListener {
            mAuth = Firebase.auth
            session.userid = mAuth.uid!!
            var chat : Chats = Chats()
            var exists : Boolean = false

            database = FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid!!).child("Chats")
            var userchatarray: MutableList<String> = mutableListOf()

            database.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()){
                        for (data in snapshot.children){
                            val myclass = data.getValue(String::class.java)
                            if (myclass != null) {
                                userchatarray.add(myclass)
                            }
                        }
                        database = FirebaseDatabase.getInstance().getReference("Mentor").child(mentor.id).child("Chats")

                        database.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()){
                                    for (data in snapshot.children){
                                        val myclass = data.getValue(String::class.java)
                                        for (temp in userchatarray){
                                            if (myclass != null) {
                                                Log.w("TAG", temp)
                                                Log.w("TAG", myclass)
                                                if (temp == myclass){
                                                    exists = true
                                                    Log.w("TAG", "Chat exists")
                                                    FirebaseDatabase.getInstance().getReference("Chat").child(temp).addListenerForSingleValueEvent(object : ValueEventListener {
                                                        override fun onDataChange(snapshot: DataSnapshot) {
                                                            if (snapshot.exists()){
                                                                val temp = snapshot.getValue(Chats::class.java)
                                                                if (temp != null){
                                                                    chat.id = temp.id
                                                                    chat.pic = temp.pic
                                                                    val temp = Intent(this@book_session, individual_chat::class.java )
                                                                    Log.w("TAG","Opening chat")
                                                                    exists = true
                                                                    temp.putExtra("object", chat)
                                                                    startActivity(temp)
                                                                }
                                                            }
                                                        }
                                                        override fun onCancelled(error: DatabaseError){}
                                                    })
                                                }
                                            }
                                        }
                                    }

                                }else {
                                    createchat(mentor)
                                }
                            }
                            override fun onCancelled(error: DatabaseError){}
                        })
                    }else {
                        createchat(mentor)
                    }
                }
                override fun onCancelled(error: DatabaseError){}
            })
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

    private fun createchat(mentor: Mentors){
        val chat : Chats = Chats()
        database = FirebaseDatabase.getInstance().getReference("Chat").child(chat.id)

        chat.id = database.push().key.toString()
        chat.pic = mentor.profilepic
        database.child(chat.id).setValue(chat).addOnCompleteListener {
            Log.w("TAG", "Review uploaded")
            finish()
        }.addOnFailureListener{
            Log.w("TAG", "Didnt upload Review")
        }

        database = FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid!!)
        database.child("Chats").child(chat.id).setValue(chat.id)

        database = FirebaseDatabase.getInstance().getReference("Mentor").child(mentor.id)
        database.child("Chats").child(chat.id).setValue(chat.id)

        val temp = Intent(this@book_session, individual_chat::class.java )
        temp.putExtra("object", chat)
        startActivity(temp)
    }
}