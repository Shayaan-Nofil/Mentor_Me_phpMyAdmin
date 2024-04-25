package com.ShayaanNofil.i210450

import Mentors
import Reviews
import User
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
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
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import kotlin.random.Random

private lateinit var storage: FirebaseStorage
private lateinit var database: DatabaseReference
private lateinit var mAuth: FirebaseAuth
class profile_page : AppCompatActivity() {
    private lateinit var profilepicimg: CircleImageView
    private lateinit var bgpicimg: ImageView
    private var profilePictureUri: Uri? = null
    private var backPictureUri: Uri? = null
    lateinit var usr: User
    var img: Bitmap?=null
    private var server_ip = "http://192.168.18.70//"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_page)

        profilepicimg = findViewById(R.id.ali_profilepic)
        bgpicimg = findViewById(R.id.background_img)

        usr = intent.getSerializableExtra("user") as User
        val nametext: TextView = findViewById(R.id.username_text)
        nametext.text = usr.name
        getdata()
        updateimg()


//        mAuth = Firebase.auth
//        var userId = mAuth.uid;
//        usr = User()
//        usr.id = userId!!
//        database = FirebaseDatabase.getInstance().getReference("User").child(userId)
//
//        database.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                //usr.id = snapshot.child("id").value.toString()
//                usr.name = snapshot.child("name").value.toString()
//                usr.email = snapshot.child("email").value.toString()
//                usr.country = snapshot.child("country").value.toString()
//                usr.city = snapshot.child("city").value.toString()
//                usr.number = snapshot.child("number").value.toString()
//                usr.profilepic = snapshot.child("profilepic").value.toString()
//                usr.bgpic = snapshot.child("bgpic").value.toString()
//                nametext.text = usr.name
//                usr.bgpic = usr.bgpic
//                updateimg()
//            }
//            override fun onCancelled(error: DatabaseError){}
//        })
//
//        val recycle_topmentor: RecyclerView = findViewById(R.id.recycle_favorite_mentors)
//        recycle_topmentor.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL, false)
//        val mentorarray: MutableList<Mentors> = mutableListOf()
//
//        FirebaseDatabase.getInstance().getReference("Mentor").addValueEventListener(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                mentorarray.clear()
//                if (snapshot.exists()){
//                    for (data in snapshot.children){
//                        val myclass = data.getValue(Mentors::class.java)
//                        mentorarray.add(myclass!!)
//                    }
//
//                    val adapter = homerecycle_adapter(mentorarray)
//                    recycle_topmentor.adapter = adapter
//
//                    adapter.setOnClickListener(object :
//                        homerecycle_adapter.OnClickListener {
//                        override fun onClick(position: Int, model: Mentors) {
//                            val intent = Intent(this@profile_page, john_profile::class.java)
//                            intent.putExtra("object", model)
//                            startActivity(intent)
//                        }
//                    })
//
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })
//
//
//
//
//        val recycle_reviews: RecyclerView = findViewById(R.id.recycle_reviews)
//        recycle_reviews.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//        val reviewarray: MutableList<Reviews> = mutableListOf()
//
//        FirebaseDatabase.getInstance().getReference("Review").addValueEventListener(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                reviewarray.clear()
//                if (snapshot.exists()){
//                    for (data in snapshot.children){
//                        val myclass = data.getValue(Reviews::class.java)
//                        if (myclass != null) {
//                            if (myclass.userid == userId)
//                                reviewarray.add(myclass)
//                        }
//                    }
//
//                    val adapter = review_recycle_adapter(reviewarray)
//                    recycle_reviews.adapter = adapter
//
//                }
//            }
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        })


        val editbutton=findViewById<View>(R.id.edit_pfpic_bt)
        editbutton.setOnClickListener {
            openGalleryForImage()
        }

        val editbackground: CircleImageView = findViewById(R.id.edit_bgpic_bt)
        editbackground.setOnClickListener(View.OnClickListener {
            openGalleryForImage2()
        })

        val editprofile : ImageButton = findViewById(R.id.bt_settings)
        editprofile.setOnClickListener(View.OnClickListener {
            showOptionsDialog()
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
            img = MediaStore.Images.Media.getBitmap(contentResolver,uri)
            Glide.with(this).load(uri).into(profilepicimg)

            updatepictures("profilepic")
        }
    }
    private val gallerybackgroundLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        bgpicimg.setImageURI(uri)
        if (uri != null){
            backPictureUri = uri
            usr.bgpic = uri.toString()
            img = MediaStore.Images.Media.getBitmap(contentResolver,uri)
            Glide.with(this).load(uri).into(bgpicimg)

            updatepictures("bgpic")
        }
    }
    private fun openGalleryForImage() {
        galleryprofileLauncher.launch("image/*")
    }

    private fun openGalleryForImage2() {
        gallerybackgroundLauncher.launch("image/*")
    }

    private fun updatepictures(typepic: String) {
        val requestQueue = Volley.newRequestQueue(this)
        val serverUrl = server_ip + "uploaddp.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST,
            serverUrl,
            Response.Listener { response ->
                Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                Log.e("response", response)
                val res = JSONObject(response)
                var url = "http://192.168.18.70/" + res.get("url")

                if (typepic == "profilepic"){
                    usr.profilepic = url
                }
                else{
                    usr.bgpic = url
                }

            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                Log.e("error", error.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["userid"] = usr.id
                params["pictype"] = typepic
                params.put("image",bitmapToBase64(img!!))
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    fun bitmapToBase64(dp:Bitmap):String{
        var stream= ByteArrayOutputStream()
        dp.compress(Bitmap.CompressFormat.JPEG,100,stream)
        stream.toByteArray()
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT).toString()

    }

    fun updateimg(){
        if (usr.profilepic.isNotEmpty()){
            Log.w("TAG", "Updating image")
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

    private fun showOptionsDialog() {
        val options = arrayOf("Edit Profile", "Logout")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Options")
        builder.setItems(options) { dialogInterface: DialogInterface, which: Int ->
            when (which) {
                0 -> {
                    val temp = Intent(this, edit_profile::class.java )
                    temp.putExtra("user", usr)
                    startActivity(temp)
                }
                1 -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, Log_in_page::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
        }
        builder.create().show()
    }

    private fun getdata(){
        val url = server_ip + "getuserdata.php"

        val params = HashMap<String, String>()
        params["userId"] = usr.id

        val requestQueue = Volley.newRequestQueue(this)
        val stringRequest = object : StringRequest(
            Request.Method.POST,
            url,
            Response.Listener<String> { response ->
                // Parse the JSON response
                val userJson = JSONObject(response)
                // Get the user data from the JSON object
                usr.id = userJson.getString("id")
                usr.name = userJson.getString("name")
                usr.email = userJson.getString("email")
                usr.number = userJson.getString("number")
                usr.city = userJson.getString("city")
                usr.country = userJson.getString("country")
                usr.token = userJson.getString("token")
                usr.profilepic = userJson.getString("profilepic")
                usr.bgpic = userJson.getString("bgpic")
            },
            Response.ErrorListener { error ->
            }
        ) {
            override fun getParams(): Map<String, String> {
                return params
            }
        }
    }
}