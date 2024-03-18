package com.ShayaanNofil.i210450

import Mentors
import Reviews
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

class review_recycle_adapter(private val items: MutableList<Reviews>): RecyclerView.Adapter<review_recycle_adapter.ViewHolder>() {

    class ViewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        val mentorname: TextView = itemView.findViewById(R.id.mentor_name)
        val starone: ImageView = itemView.findViewById(R.id.starone)
        val startwo: ImageView = itemView.findViewById(R.id.startwo)
        val starthree: ImageView = itemView.findViewById(R.id.starthree)
        val starfour: ImageView = itemView.findViewById(R.id.starfour)
        val starfive: ImageView = itemView.findViewById(R.id.starfive)
        val reviewdesc: TextView = itemView.findViewById(R.id.review_description)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_recycler, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val review = items[position]

        holder.mentorname.text = review.mentorname
        holder.reviewdesc.text = review.comments
        var rating = review.rating

        if (rating == 1.0){
            holder.starone.setBackgroundResource(R.drawable.gold_star)
            holder.startwo.setBackgroundResource(R.drawable.gold_star_unfilled)
            holder.starthree.setBackgroundResource(R.drawable.gold_star_unfilled)
            holder.starfour.setBackgroundResource(R.drawable.gold_star_unfilled)
            holder.starfive.setBackgroundResource(R.drawable.gold_star_unfilled)
        }
        else if (rating == 2.0){
            holder.starone.setBackgroundResource(R.drawable.gold_star)
            holder.startwo.setBackgroundResource(R.drawable.gold_star)
            holder.starthree.setBackgroundResource(R.drawable.gold_star_unfilled)
            holder.starfour.setBackgroundResource(R.drawable.gold_star_unfilled)
            holder.starfive.setBackgroundResource(R.drawable.gold_star_unfilled)
        }
        else if (rating == 3.0){
            holder.starone.setBackgroundResource(R.drawable.gold_star)
            holder.startwo.setBackgroundResource(R.drawable.gold_star)
            holder.starthree.setBackgroundResource(R.drawable.gold_star)
            holder.starfour.setBackgroundResource(R.drawable.gold_star_unfilled)
            holder.starfive.setBackgroundResource(R.drawable.gold_star_unfilled)
        }
        else if (rating == 4.0){
            holder.starone.setBackgroundResource(R.drawable.gold_star)
            holder.startwo.setBackgroundResource(R.drawable.gold_star)
            holder.starthree.setBackgroundResource(R.drawable.gold_star)
            holder.starfour.setBackgroundResource(R.drawable.gold_star)
            holder.starfive.setBackgroundResource(R.drawable.gold_star_unfilled)
        }
        else {
            holder.starone.setBackgroundResource(R.drawable.gold_star)
            holder.startwo.setBackgroundResource(R.drawable.gold_star)
            holder.starthree.setBackgroundResource(R.drawable.gold_star)
            holder.starfour.setBackgroundResource(R.drawable.gold_star)
            holder.starfive.setBackgroundResource(R.drawable.gold_star)
        }
    }

}