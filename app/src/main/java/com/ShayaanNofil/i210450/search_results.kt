package com.ShayaanNofil.i210450

import Mentors
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import searchrecycle_adapter

class search_results : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)
        val intent = intent
        val mentorname = intent.getStringExtra("mentor").toString()

        val recycle_topmentor: RecyclerView = findViewById(R.id.searchresults_recycle)
        recycle_topmentor.layoutManager = LinearLayoutManager(this)

        val mentorarray: MutableList<Mentors> = mutableListOf()
        Log.w("TAG", mentorname)

        FirebaseDatabase.getInstance().getReference("Mentor").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mentorarray.clear()
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(Mentors::class.java)
                        if (myclass != null) {
                            if (mentorname.isNotEmpty()){
                                if (myclass.name == mentorname){
                                    mentorarray.add(myclass)
                                }
                            }
                            else{
                                mentorarray.add(myclass)
                            }

                        }
                    }
                    Log.w("TAG", "Display recycle")
                    val adapter = searchrecycle_adapter(mentorarray)
                    recycle_topmentor.adapter = adapter

                    adapter.setOnClickListener(object :
                        searchrecycle_adapter.OnClickListener {
                        override fun onClick(position: Int, model: Mentors) {
                            val intent = Intent(this@search_results, book_session::class.java)
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
}