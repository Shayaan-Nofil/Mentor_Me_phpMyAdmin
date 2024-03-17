package com.ShayaanNofil.i210450

import Sessions
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import booked_session_recycle_adapter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var mAuth: FirebaseAuth
class booked_sessions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booked_sessions)

        mAuth = Firebase.auth

        val recycle_topmentor: RecyclerView = findViewById(R.id.session_recycle)
        recycle_topmentor.layoutManager = LinearLayoutManager(this)

        val sessionarray: MutableList<Sessions> = mutableListOf()

        FirebaseDatabase.getInstance().getReference("Session").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sessionarray.clear()
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(Sessions::class.java)
                        val userid = mAuth.uid
                        if (myclass != null) {
                            if (myclass.userid == userid){
                                sessionarray.add(myclass)
                            }
                        }
                    }
                    Log.w("TAG", "Display recycle")
                    val adapter = booked_session_recycle_adapter(sessionarray)
                    recycle_topmentor.adapter = adapter

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
    }
}