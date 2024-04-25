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
class chatsearch_recycle_adapter(private val items: MutableList<Chats>, private val user: User): RecyclerView.Adapter<chatsearch_recycle_adapter.ViewHolder>() {
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
        if (user.id.toInt() == chat.userid){
            holder.personname.text = chat.mentorname

            Glide.with(holder.itemView)
                .load(chat.mentorimg)
                .into(holder.profleimg)
        }
        else{
            holder.personname.text = chat.username

            Glide.with(holder.itemView)
                .load(chat.userimg)
                .into(holder.profleimg)
        }

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

}