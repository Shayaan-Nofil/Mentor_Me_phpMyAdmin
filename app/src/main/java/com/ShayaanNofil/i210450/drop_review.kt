package com.ShayaanNofil.i210450

import Mentors
import Reviews
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
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView

private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
class drop_review : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drop_review)

        val mentor: Mentors = intent.getSerializableExtra("object") as Mentors
        var review: Reviews = Reviews()

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
            mAuth = Firebase.auth
            review.userid = mAuth.uid!!
            val revdesc : EditText = findViewById(R.id.review_para)
            review.comments = revdesc.text.toString()

            database = FirebaseDatabase.getInstance().getReference("Review")
            review.id = database.push().key.toString()

            database.child(review.id).setValue(review).addOnCompleteListener {
                Log.w("TAG", "Review uploaded")
                finish()
            }.addOnFailureListener{
                Log.w("TAG", "Didnt upload Review")
            }
        })

    }
}