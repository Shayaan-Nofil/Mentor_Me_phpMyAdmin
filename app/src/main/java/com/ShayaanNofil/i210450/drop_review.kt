package com.ShayaanNofil.i210450

import Mentors
import Reviews
import User
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView

private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
class drop_review : AppCompatActivity() {
    private var server_ip = "http://192.168.68.184//"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drop_review)

        val mentor: Mentors = intent.getSerializableExtra("object") as Mentors
        val user = intent.getSerializableExtra("user") as? User
        var review = Reviews()

        val mentorname: TextView = findViewById(R.id.greeting_text)
        val tempname = "Hi, I'm\n" + mentor.name
        mentorname.text = tempname

        val mentorimg: CircleImageView = findViewById(R.id.mentor_img)
        Glide.with(this).load(mentor.profilepic).into(mentorimg)

        val onestar: ImageButton = findViewById(R.id.onestar)
        val twostar: Button = findViewById(R.id.twostar)
        val threestar: Button = findViewById(R.id.threestar)
        val fourstar: Button = findViewById(R.id.fourstar)
        val fivestar: Button = findViewById(R.id.fivestar)

        onestar.setOnClickListener(View.OnClickListener {
            onestar.setBackgroundResource(R.drawable.gold_star)
            twostar.setBackgroundResource(R.drawable.ratingstar_unfilled)
            threestar.setBackgroundResource(R.drawable.ratingstar_unfilled)
            fourstar.setBackgroundResource(R.drawable.ratingstar_unfilled)
            fivestar.setBackgroundResource(R.drawable.ratingstar_unfilled)
            review.rating = 1.0
        })

        twostar.setOnClickListener(View.OnClickListener {
            onestar.setBackgroundResource(R.drawable.gold_star)
            twostar.setBackgroundResource(R.drawable.gold_star)
            threestar.setBackgroundResource(R.drawable.ratingstar_unfilled)
            fourstar.setBackgroundResource(R.drawable.ratingstar_unfilled)
            fivestar.setBackgroundResource(R.drawable.ratingstar_unfilled)
            review.rating = 2.0
        })

        threestar.setOnClickListener(View.OnClickListener {
            onestar.setBackgroundResource(R.drawable.gold_star)
            twostar.setBackgroundResource(R.drawable.gold_star)
            threestar.setBackgroundResource(R.drawable.gold_star)
            fourstar.setBackgroundResource(R.drawable.ratingstar_unfilled)
            fivestar.setBackgroundResource(R.drawable.ratingstar_unfilled)
            review.rating = 3.0
        })

        fourstar.setOnClickListener(View.OnClickListener {
            onestar.setBackgroundResource(R.drawable.gold_star)
            twostar.setBackgroundResource(R.drawable.gold_star)
            threestar.setBackgroundResource(R.drawable.gold_star)
            fourstar.setBackgroundResource(R.drawable.gold_star)
            fivestar.setBackgroundResource(R.drawable.ratingstar_unfilled)
            review.rating = 4.0
        })

        fivestar.setOnClickListener(View.OnClickListener {
            onestar.setBackgroundResource(R.drawable.gold_star)
            twostar.setBackgroundResource(R.drawable.gold_star)
            threestar.setBackgroundResource(R.drawable.gold_star)
            fourstar.setBackgroundResource(R.drawable.gold_star)
            fivestar.setBackgroundResource(R.drawable.gold_star)
            review.rating = 5.0
        })

        val backbutton=findViewById<View>(R.id.back_button)
        backbutton.setOnClickListener(View.OnClickListener {
            finish()
        })

        val subbutton=findViewById<View>(R.id.submit_button)
        subbutton.setOnClickListener(View.OnClickListener {
            review.mentorid = mentor.id
            review.mentorname = mentor.name
            review.userid = user!!.id

            val revdesc : EditText = findViewById(R.id.review_para)
            review.comments = revdesc.text.toString()

            val serverUrl = server_ip + "create_review.php"
            val requestQueue = Volley.newRequestQueue(this)

            val stringRequest = object : StringRequest(
                Method.POST, serverUrl,
                Response.Listener<String> { response ->
                    // Parse the response from the server
                    finish()
                },
                Response.ErrorListener { error ->
                    // Handle error
                    Log.w("TAG", "createUserWithEmail:failure")
                    val text = "Didn't work"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this, text, duration) // in Activity
                    toast.show()
                }
            ) {
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    val gson = Gson()
                    val userJson = gson.toJson(review)
                    params["review"] = userJson
                    return params
                }
            }
            requestQueue.add(stringRequest)
        })

    }
}