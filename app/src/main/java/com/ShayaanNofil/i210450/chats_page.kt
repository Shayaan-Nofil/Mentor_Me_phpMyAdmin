package com.ShayaanNofil.i210450

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class chats_page : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    //private lateinit var chatAdapter: chat_adapter
    //private var chatList: ArrayList<Chat> = ArrayList() // Initialize this list with your chat data
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chats_page)

//        recyclerView = findViewById(R.id.recycler_view)
//
//        recyclerView.layoutManager = LinearLayoutManager(this)

//        chatAdapter = chat_adapter(this, chatList)
//        recyclerView.adapter = chatAdapter

        //getChats()

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val chatbutton=findViewById<View>(R.id.recycler_view)
        chatbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, individual_chat::class.java )
            startActivity(temp)
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

//        fun getChats(){
//
//            var firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!
//            var databaseReference:DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")
//
//            databaseReference.addValueEventListener(object :ValueEventListener{
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    chatList.clear()
//
//                    for(dataSnapShot:DataSnapshot in snapshot.children){
//                        val user = dataSnapShot.getValue(Chat::class.java)
//
//                        if(user!!.userId.equals(firebase.uid) ){
//                            chatList.add(user)
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(applicationContext,error.message,Toast.LENGTH_SHORT).show()
//                }
//
//            })

//            chatAdapter = chat_adapter(this, chatList)
//            recyclerView.adapter = chatAdapter
            }
        }
