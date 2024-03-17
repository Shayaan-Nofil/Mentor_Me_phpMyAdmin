package com.ShayaanNofil.i210450

import Mentors
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var database: DatabaseReference
private lateinit var mAuth: FirebaseAuth
class home_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        mAuth = Firebase.auth
        var userId = mAuth.uid;
        database = FirebaseDatabase.getInstance().getReference("User").child(userId!!)

        val nametext: TextView = findViewById(R.id.user_name_text)
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                nametext.text = snapshot.child("name").value.toString()
            }
            override fun onCancelled(error: DatabaseError){}
        })

        val recycle_topmentor: RecyclerView = findViewById(R.id.recycle_top_mentors)
        val recycle_education: RecyclerView = findViewById(R.id.recycle_education)
        val recycle_recent: RecyclerView = findViewById(R.id.recycle_recent)
        recycle_topmentor.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        recycle_education.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        recycle_recent.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
        val mentorarray: MutableList<Mentors> = mutableListOf()

        FirebaseDatabase.getInstance().getReference("Mentor").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mentorarray.clear()
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(Mentors::class.java)
                        mentorarray.add(myclass!!)
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
                            startActivity(intent)
                        }
                    })

                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })



        val notifbutton=findViewById<View>(R.id.notif_button)
        notifbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, notifications::class.java )
            startActivity(temp)
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
}
