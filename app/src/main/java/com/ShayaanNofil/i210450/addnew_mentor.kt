package com.ShayaanNofil.i210450

import Mentors
import User
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONObject
import java.io.ByteArrayOutputStream

class addnew_mentor : AppCompatActivity() {
    private var mentorimgUri: Uri? = null
    lateinit var mentor: Mentors
    lateinit var uppic: ImageView
    var img: Bitmap?=null
    private var server_ip = "http://192.168.68.184//"
    lateinit var usr: User
    private var typeofuser = "user"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnew_mentor)
        mentor = Mentors()

        usr = intent.getSerializableExtra("user") as User
        typeofuser = intent.getStringExtra("typeofuser").toString()

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val uploadbutton=findViewById<View>(R.id.upload_button)
        uploadbutton.setOnClickListener(View.OnClickListener {
            val mentorname: EditText = findViewById(R.id.name_box)
            val mentordisc: EditText = findViewById(R.id.desc_box)
            val mentorstat: Spinner = findViewById(R.id.status_box)
            mentor.name = mentorname.text.toString()
            mentor.description = mentordisc.text.toString()
            mentor.status = mentorstat.selectedItem.toString()
            creatementor()
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
            temp.putExtra("user", usr)
            temp.putExtra("typeofuser", typeofuser)
            startActivity(temp)
        })

        val task_searchbutton=findViewById<View>(R.id.btsearch)
        task_searchbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, Search::class.java )
            temp.putExtra("user", usr)
            temp.putExtra("typeofuser", typeofuser)
            startActivity(temp)
        })

        val task_chatbutton=findViewById<View>(R.id.btchat)
        task_chatbutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, chats_page::class.java )
            temp.putExtra("user", usr)
            temp.putExtra("typeofuser", typeofuser)
            startActivity(temp)
        })

        val task_profilebutton=findViewById<View>(R.id.btprofile)
        task_profilebutton.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, profile_page::class.java )
            temp.putExtra("user", usr)
            temp.putExtra("typeofuser", typeofuser)
            startActivity(temp)
        })

        val task_addcontent=findViewById<View>(R.id.btaddcontent)
        task_addcontent.setOnClickListener(View.OnClickListener {
            val temp = Intent(this, camera_picture_mode::class.java )
            temp.putExtra("user", usr)
            temp.putExtra("typeofuser", typeofuser)
            startActivity(temp)
        })
    }

    private fun creatementor() {
        val requestQueue = Volley.newRequestQueue(this)
        val serverUrl = server_ip + "creatementor.php"

        val stringRequest = object : StringRequest(
            Request.Method.POST,
            serverUrl,
            Response.Listener { response ->
                Toast.makeText(this, response, Toast.LENGTH_LONG).show()
                Log.e("response", response)
                val res = JSONObject(response)
                var url = "http://192.168.18.70/" + res.get("url")
                mentor.profilepic = url

                finish()
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show()
                Log.e("error", error.toString())
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                val gson = Gson()
                val userJson = gson.toJson(mentor)
                params["Mentors"] = userJson
                params.put("image",bitmapToBase64(img!!))
                return params
            }
        }
        requestQueue.add(stringRequest)
    }

    fun bitmapToBase64(dp: Bitmap):String{
        var stream= ByteArrayOutputStream()
        dp.compress(Bitmap.CompressFormat.JPEG,100,stream)
        stream.toByteArray()
        return Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT).toString()

    }

    private val galleryprofileLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uppic.setImageURI(uri)
        if (uri != null){
            img = MediaStore.Images.Media.getBitmap(contentResolver,uri)
        }
    }
    private fun openGalleryForImage() {
        galleryprofileLauncher.launch("image/*")
    }
}