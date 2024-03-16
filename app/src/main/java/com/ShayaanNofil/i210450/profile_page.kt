package com.ShayaanNofil.i210450

import User
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import de.hdodenhof.circleimageview.CircleImageView

private lateinit var storage: FirebaseStorage
private lateinit var database: DatabaseReference
private lateinit var mAuth: FirebaseAuth
class profile_page : AppCompatActivity() {
    private lateinit var profilepicimg: CircleImageView
    private lateinit var bgpicimg: ImageView
    private var profilePictureUri: Uri? = null
    private var backPictureUri: Uri? = null
    lateinit var usr: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        mAuth = Firebase.auth
        var userId = mAuth.uid;
        usr = User()
        usr.id = userId!!
        database = FirebaseDatabase.getInstance().getReference("User").child(userId)

        var nametext: TextView = findViewById(R.id.username_text)
        profilepicimg = findViewById(R.id.ali_profilepic)
        bgpicimg = findViewById(R.id.background_img)

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                //usr.id = snapshot.child("id").value.toString()
                usr.name = snapshot.child("name").value.toString()
                usr.email = snapshot.child("email").value.toString()
                usr.country = snapshot.child("country").value.toString()
                usr.city = snapshot.child("city").value.toString()
                usr.number = snapshot.child("number").value.toString()
                usr.profilepic = snapshot.child("profilepic").value.toString()
                usr.bgpic = snapshot.child("bgpic").value.toString()
                nametext.text = usr.name
                usr.profilepic = usr.profilepic
                usr.bgpic = usr.bgpic
                updateimg()
            }
            override fun onCancelled(error: DatabaseError){}
        })

        Log.w("TAG", usr.id)

        val editbutton=findViewById<View>(R.id.edit_pfpic_bt)
        editbutton.setOnClickListener {
            openGalleryForImage()
        }

        val editbackground: CircleImageView = findViewById(R.id.edit_bgpic_bt)
        editbackground.setOnClickListener(View.OnClickListener {
            openGalleryForImage2()
        })

        val editprofile : Button = findViewById(R.id.bt_settings)
        editprofile.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, edit_profile::class.java )
            startActivity(temp)
            //Firebase.auth.signOut()
        })

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val sessionbutton=findViewById<View>(R.id.sessions_button)
        sessionbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, booked_sessions::class.java )
            startActivity(temp)
        })

        val goprofilebutton=findViewById<View>(R.id.john_profile_button)
        goprofilebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, john_profile::class.java )
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
    private val galleryprofileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        profilepicimg.setImageURI(uri)
        if (uri != null){
            profilePictureUri = uri
            usr.profilepic = uri.toString()
            updateprofilepic()
        }
    }
    private val gallerybackgroundLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        bgpicimg.setImageURI(uri)
        if (uri != null){
            backPictureUri = uri
            usr.bgpic = uri.toString()
            updatebackgroundpic()
        }
    }
    private fun openGalleryForImage() {
        galleryprofileLauncher.launch("image/*")
    }

    private fun openGalleryForImage2() {
        gallerybackgroundLauncher.launch("image/*")
    }


    private fun updateprofilepic(){
        storage = FirebaseStorage.getInstance()
        val storageref = storage.getReference("Images")
        val userId = mAuth.uid;

        profilePictureUri?. let{
            storageref.child(userId!!).putFile(profilePictureUri!!).addOnSuccessListener {
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {task ->
                    usr.profilepic = task.toString()
                    Log.w("TAG", "Upload Success")

                    database.setValue(usr).addOnSuccessListener {
                        profilepicimg.setImageURI(profilePictureUri)
                    }
                }
            }.addOnFailureListener{
                Log.w("TAG", "Upload failed")
            }
        }
    }

    private fun updatebackgroundpic(){
        storage = FirebaseStorage.getInstance()
        val storageref = storage.getReference("Images")
        val userId = mAuth.uid;

        backPictureUri?. let{
            storageref.child(userId!!).putFile(backPictureUri!!).addOnSuccessListener {
                it.metadata!!.reference!!.downloadUrl.addOnSuccessListener {task ->
                    usr.bgpic = task.toString()
                    Log.w("TAG", "Upload Success")

                    database.setValue(usr).addOnSuccessListener {
                        bgpicimg.setImageURI(backPictureUri)
                    }
                }
            }.addOnFailureListener{
                Log.w("TAG", "Upload failed")
            }
        }
    }
    fun updateimg(){
        if (usr.profilepic.isNotEmpty()){
            Glide.with(this)
                .load(usr.profilepic)
                .into(profilepicimg)
        }
        if (usr.bgpic.isNotEmpty()){
            Glide.with(this)
                .load(usr.bgpic)
                .into(bgpicimg)
        }
    }
}