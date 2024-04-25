package com.ShayaanNofil.i210450

import Mentors
import User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

class john_profile() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_john_profile)
        val mentor: Mentors = intent.getSerializableExtra("object") as Mentors
        val user = intent.getSerializableExtra("user") as? User

        var mentorname: TextView = findViewById(R.id.name_text)
        val tempname = "Hi, I'm\n" + mentor.name
        mentorname.text = tempname

        var mentorimg: CircleImageView = findViewById(R.id.johnimg)
        Glide.with(this).load(mentor.profilepic).into(mentorimg)

        var mentorjob: TextView = findViewById(R.id.review_text)
        mentorjob.text = mentor.job

        var mentordesc: TextView = findViewById(R.id.description_text)
        mentordesc.text = mentor.description

        var mentorrate: TextView = findViewById(R.id.rating_text)
        mentorrate.text = mentor.rating.toString()

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val revbutton=findViewById<View>(R.id.bt_drop_rev)
        revbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, drop_review::class.java )
            temp.putExtra("object", mentor)
            temp.putExtra("user", user)
            startActivity(temp)
        })

        val combutton=findViewById<View>(R.id.bt_join_com)
        combutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, community_chat::class.java )
            startActivity(temp)
        })
    }
}