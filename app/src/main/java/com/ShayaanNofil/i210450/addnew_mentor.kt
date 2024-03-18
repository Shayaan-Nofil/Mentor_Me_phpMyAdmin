package com.ShayaanNofil.i210450

import Mentors
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlin.random.Random

private lateinit var storage: FirebaseStorage
private lateinit var database: DatabaseReference
private lateinit var mAuth: FirebaseAuth
class addnew_mentor : AppCompatActivity() {
    private var mentorimgUri: Uri? = null
    lateinit var mentor: Mentors
    lateinit var uppic: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnew_mentor)
        mentor = Mentors()

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val uploadbutton=findViewById<View>(R.id.upload_button)
        uploadbutton.setOnClickListener(View.OnClickListener {
            val mentorname : EditText = findViewById(R.id.name_box)
            val mentordisc : EditText = findViewById(R.id.desc_box)
            val mentorstat : Spinner = findViewById(R.id.status_box)
            mentor.name = mentorname.text.toString()
            mentor.description = mentordisc.text.toString()
            mentor.status = mentorstat.selectedItem.toString()

            storage = FirebaseStorage.getInstance()
            val storageref = storage.reference
            database = FirebaseDatabase.getInstance().getReference("Mentor")

            if (mentorimgUri != null){
                Log.w("TAG", "Uploading")
                storageref.child(mentor.name + Random.nextInt(0,100000).toString()).putFile(mentorimgUri!!).addOnSuccessListener {
                    it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {task ->
                        mentor.profilepic = task.toString()
                        Log.w("TAG", "Upload Success")
                        mentor.id = database.push().key.toString()

                        database.child(mentor.id).setValue(mentor).addOnSuccessListener {
                            finish()
                        }
                    }
                }.addOnFailureListener{
                    Log.w("TAG", "Upload failed")
                }
            }
            else{
                mentor.id = database.push().key.toString()
                database.setValue(mentor).addOnSuccessListener {
                    finish()
                }
            }
        })

        val upvid=findViewById<View>(R.id.up_vid)
        upvid.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, camera_video_mode::class.java )
            startActivity(temp)
        })

        uppic= findViewById(R.id.up_pht)
        uppic.setOnClickListener(View.OnClickListener {
            openGalleryForImage()
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
            val temp = Intent(this, camera_picture_mode::class.java )
            startActivity(temp)
        })
    }
    private val galleryprofileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uppic.setImageURI(uri)
        if (uri != null){
            mentorimgUri = uri
        }
    }
    private fun openGalleryForImage() {
        galleryprofileLauncher.launch("image/*")
    }
}