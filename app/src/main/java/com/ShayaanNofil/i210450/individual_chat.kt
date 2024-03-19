package com.ShayaanNofil.i210450

import Chats
import Mentors
import Messages
import User
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import chat_recycle_adapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.inappmessaging.model.Button
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.random.Random

private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
class individual_chat : AppCompatActivity() {
    private var messages: Messages? = null
    private var messageimg: Uri? = null
    private var uploadimgbt : ImageButton? = null
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_individual_chat)

        val chat: Chats = intent.getSerializableExtra("object") as Chats

        var chattername : TextView = findViewById(R.id.chatter_name)
        mAuth = Firebase.auth

        //Get the appropriate name of the chatter
        FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    Log.w("TAG", "User is a user")
                    database = FirebaseDatabase.getInstance().getReference("Mentor")

                    database.addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (data in snapshot.children) {
                                    val mentor = data.getValue(Mentors::class.java)
                                    if (mentor != null) {
                                        FirebaseDatabase.getInstance().getReference("Mentor").child(mentor.id!!).child("Chats").addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                if (snapshot.exists()) {
                                                    for (data in snapshot.children) {
                                                        val chats = data.getValue(String::class.java)
                                                        if (chats != null) {
                                                            if (chats == chat.id) {
                                                                chattername.text = mentor.name
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            override fun onCancelled(error: DatabaseError) {}
                                        })
                                    }
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
                else{
                    Log.w("TAG", "User is a mentor")

                        FirebaseDatabase.getInstance().getReference("User").addListenerForSingleValueEvent(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                for (data in snapshot.children) {
                                    val mentor = data.getValue(User::class.java)
                                    if (mentor != null) {
                                        FirebaseDatabase.getInstance().getReference("User").child(mentor.id!!).child("Chats").addListenerForSingleValueEvent(object : ValueEventListener {
                                            override fun onDataChange(snapshot: DataSnapshot) {
                                                Log.w("TAG", "Mentor | Got chats")
                                                if (snapshot.exists()) {
                                                    for (data in snapshot.children) {
                                                        val chats = data.getValue(String::class.java)
                                                        if (chats != null) {
                                                            if (chats == chat.id) {
                                                                chattername.text = mentor.name
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            override fun onCancelled(error: DatabaseError) {}
                                        })
                                    }
                                }
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {}
                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })


        val recycle_topmentor: RecyclerView = findViewById(R.id.messages_recycler)
        recycle_topmentor.layoutManager = LinearLayoutManager(this)

        val messagearray: MutableList<Messages> = mutableListOf()

        FirebaseDatabase.getInstance().getReference("Chat").child(chat.id).child("Messages").addValueEventListener(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messagearray.clear()
                if (snapshot.exists()){
                    for (data in snapshot.children){
                        val myclass = data.getValue(Messages::class.java)
                        if (myclass != null) {
                            messagearray.add(myclass)
                        }
                    }
                    val adapter = chat_recycle_adapter(messagearray)
                    recycle_topmentor.adapter = adapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        val btsend: android.widget.Button = findViewById(R.id.btsend)
        btsend.setOnClickListener(View.OnClickListener {
            var message: Messages = Messages()
            mAuth = Firebase.auth
            if (messages == null){ //Text Messages
                val messagecontent : EditText = findViewById(R.id.message_text)
                message.content = messagecontent.text.toString()
                messagecontent.setText("")
                message.time = SimpleDateFormat("HH:mm").format(Date())
                message.senderid = mAuth.uid.toString()
                message.tag = "text"

                FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val user = snapshot.getValue(Mentors::class.java)
                            if (user != null) {
                                Log.w("TAG", "in user, getting url")
                                Log.w("TAG", user.profilepic.toString())
                                message.senderpic = user.profilepic.toString()

                                FirebaseDatabase.getInstance().getReference("Chat").child(chat.id).child("Messages").push().setValue(message)
                            }
                        }
                        else{
                            FirebaseDatabase.getInstance().getReference("Mentor").child(mAuth.uid.toString()).addListenerForSingleValueEvent(object: ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        val user = snapshot.getValue(Mentors::class.java)
                                        if (user != null) {
                                            Log.w("TAG", "in mentor, getting url")
                                            Log.w("TAG", user.profilepic.toString())
                                            message.senderpic = user.profilepic.toString()

                                            FirebaseDatabase.getInstance().getReference("Chat").child(chat.id).child("Messages").push().setValue(message)
                                        }
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {}
                            })
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {}
                })
            }
            else{ //Other messages
                messages!!.senderid = mAuth.uid.toString()
                messages!!.time = SimpleDateFormat("HH:mm").format(Date())
                message = messages!!
                messages = null
                uploadimgbt!!.background = ContextCompat.getDrawable(this, R.drawable.gallary_icon_white)

                val storageref = FirebaseStorage.getInstance().reference

                storageref.child("Chats").child(chat.id + Random.nextInt(0,100000).toString()).putFile(messageimg!!).addOnSuccessListener {
                    it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {task ->
                        message.content = task.toString()
                        Log.w("TAG", "Upload Success")

                        FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid.toString()).addListenerForSingleValueEvent(object: ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    val user = snapshot.getValue(Mentors::class.java)
                                    if (user != null) {
                                        Log.w("TAG", "in user, getting url")
                                        message.senderpic = user.profilepic.toString()

                                        FirebaseDatabase.getInstance().getReference("Chat").child(chat.id).child("Messages").push().setValue(message)
                                    }
                                }
                            }
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }.addOnFailureListener{
                    Log.w("TAG", "Upload failed")
                }
            }
            Log.w("Message uRI", message.content)

        })

        val backbutton: android.widget.Button = findViewById(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val voicebutton=findViewById<View>(R.id.voicecall_button)
        voicebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, voice_call::class.java )
            startActivity(temp)
            finish()
        })

        val videobutton=findViewById<View>(R.id.videocall_button)
        videobutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, call_screen_simple::class.java )
            startActivity(temp)
        })

        val uppic=findViewById<View>(R.id.btcamera)
        uppic.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, addnew_mentor::class.java )
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

        uploadimgbt=findViewById(R.id.btattcontent)
        uploadimgbt!!.setOnClickListener(View.OnClickListener {
            messages = Messages()
            openGalleryForImage()
        })
    }

    private val galleryprofileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null){
            messages!!.tag = "image"
            messageimg = uri

            Glide.with(this)
                .asBitmap()
                .load(messageimg)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val bitmapDrawable = BitmapDrawable(this@individual_chat.resources, resource)
                        uploadimgbt!!.background = bitmapDrawable
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle cleanup here
                    }
                })
        }
    }
    private fun openGalleryForImage() {
        galleryprofileLauncher.launch("image/*")
    }
}