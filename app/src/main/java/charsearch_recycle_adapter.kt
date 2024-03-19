import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ShayaanNofil.i210450.R
import com.bumptech.glide.Glide
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

private lateinit var mAuth: FirebaseAuth
private lateinit var database: DatabaseReference
private lateinit var typeofuser: String
class chatsearch_recycle_adapter(private val items: MutableList<Chats>): RecyclerView.Adapter<chatsearch_recycle_adapter.ViewHolder>() {
    private lateinit var onClickListener: chatsearch_recycle_adapter.OnClickListener

    class ViewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        val profleimg: CircleImageView = itemView.findViewById(R.id.profile_image)
        val personname: TextView = itemView.findViewById(R.id.chat_name)
        val missmessagecount: TextView = itemView.findViewById(R.id.new_message_count)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): chatsearch_recycle_adapter.ViewHolder {
        var onClickListener: OnClickListener? = null

        val view = LayoutInflater.from(parent.context).inflate(R.layout.chatsearch_page_recycle, parent, false)
        return chatsearch_recycle_adapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: chatsearch_recycle_adapter.ViewHolder, position: Int) {
        val chat = items[position]
        mAuth = Firebase.auth

        FirebaseDatabase.getInstance().getReference("User").child(mAuth.uid!!).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    typeofuser = "Mentor"
                    setrecyclerdata(holder, chat)
                }
                else{
                    typeofuser = "User"
                    setrecyclerdata(holder, chat)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })

        val countstring = chat.messagecount.toString() + " New Messages"
        holder.missmessagecount.text = countstring


        holder.itemView.setOnClickListener {
            onClickListener.onClick(position, chat)
        }
    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: Chats)
    }

    fun setrecyclerdata(holder: chatsearch_recycle_adapter.ViewHolder, chat: Chats){
        FirebaseDatabase.getInstance().getReference(typeofuser).addListenerForSingleValueEvent(object:
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children){
                    if (typeofuser == "Mentor"){
                        val mentor = data.getValue(Mentors::class.java)
                        if (mentor != null) {
                            FirebaseDatabase.getInstance().getReference(typeofuser).child(mentor.id).child("Chats").addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (data in snapshot.children) {
                                            val chats = data.getValue(String::class.java)
                                            if (chats != null) {
                                                if (chats == chat.id) {
                                                    holder.personname.text = mentor.name

                                                    Glide.with(holder.itemView)
                                                        .load(mentor.profilepic)
                                                        .into(holder.profleimg)
//
                                                }
                                            }
                                        }
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {}
                            })
                        }
                    }else{
                        val mentor = data.getValue(User::class.java)
                        if (mentor != null) {
                            FirebaseDatabase.getInstance().getReference(typeofuser).child(mentor.id).child("Chats").addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if (snapshot.exists()) {
                                        for (data in snapshot.children) {
                                            val chats = data.getValue(String::class.java)
                                            if (chats != null) {
                                                if (chats == chat.id) {
                                                    holder.personname.text = mentor.name

                                                    Glide.with(holder.itemView)
                                                        .load(mentor.profilepic)
                                                        .into(holder.profleimg)
//
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
            override fun onCancelled(error: DatabaseError) {
                Log.w("TAG", "Failed to read value.", error.toException())
            }
        })
    }

}