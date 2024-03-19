import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
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
import de.hdodenhof.circleimageview.CircleImageView
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

private lateinit var mAuth: FirebaseAuth
class chat_recycle_adapter(private val items: MutableList<Messages>): RecyclerView.Adapter<chat_recycle_adapter.ViewHolder>() {

    class ViewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        val senderimg: CircleImageView? = itemView.findViewById(R.id.sender_img)
        var messagecontent: TextView = itemView.findViewById(R.id.message_content)
        val messagetime: TextView = itemView.findViewById(R.id.message_time)
    }
    override fun getItemViewType(position: Int): Int {
        mAuth = Firebase.auth
        return if (items[position].senderid == mAuth.uid.toString()) {
            0 // view type for sent messages
        } else {
            1 // view type for received messages
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): chat_recycle_adapter.ViewHolder {
        mAuth = Firebase.auth
        val layoutId = if (viewType == 0) {
            R.layout.chat_message_sent_recycle
        } else {
            R.layout.chat_message_reccieved_recycle
        }
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return chat_recycle_adapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    @SuppressLint("InflateParams")
    override fun onBindViewHolder(holder: chat_recycle_adapter.ViewHolder, position: Int) {

        val message = items[position]

        if (message.tag == "image"){
            val params = holder.messagecontent.layoutParams
            params.width = 400 // replace with desired width in pixels
            params.height = 533 // replace with desired height in pixels
            holder.messagecontent.layoutParams = params
            holder.messagetime.text = message.time
            holder.messagecontent.text = ""

            Glide.with(holder.itemView)
                .asBitmap()
                .load(message.content)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        val bitmapDrawable = BitmapDrawable(holder.itemView.resources, resource)
                        holder.messagecontent.background = bitmapDrawable

                        if (message.senderid == mAuth.uid.toString()){
                            val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.chat_message_sent_recycle, null)
                            holder.messagecontent = view.findViewById(R.id.message_content)
                        }
                        else{
                            message.senderpic.let { senderpic ->
                                holder.senderimg?.let { senderimg ->
                                    Glide.with(holder.itemView)
                                        .load(senderpic)
                                        .into(senderimg)
                                }
                            }

                            val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.chat_message_reccieved_recycle, null)
                            holder.messagecontent = view.findViewById(R.id.message_content)

                        }
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // Handle cleanup here
                    }
                })
        }
        else if (message.tag == "text"){
            holder.messagecontent.text = message.content
            holder.messagetime.text = message.time

            if (message.senderid == mAuth.uid.toString()){
                val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.chat_message_sent_recycle, null)
                holder.messagecontent = view.findViewById(R.id.message_content)
            }
            else{
                message.senderpic.let { senderpic ->
                    holder.senderimg?.let { senderimg ->
                        Glide.with(holder.itemView)
                            .load(senderpic)
                            .into(senderimg)
                    }
                }

                val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.chat_message_reccieved_recycle, null)
                holder.messagecontent = view.findViewById(R.id.message_content)

            }
        }
        else if (message.tag == "audio"){
            val params = holder.messagecontent.layoutParams
            params.width = 500 // replace with desired width in pixels
            params.height = 300 // replace with desired height in pixels
            holder.messagecontent.layoutParams = params
            val content = "Audio\n" + message.content
            holder.messagecontent.text = content
            holder.messagetime.text = message.time

            if (message.senderid == mAuth.uid.toString()){
                val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.chat_message_sent_recycle, null)
                holder.messagecontent = view.findViewById(R.id.message_content)
            }
            else{
                message.senderpic.let { senderpic ->
                    holder.senderimg?.let { senderimg ->
                        Glide.with(holder.itemView)
                            .load(senderpic)
                            .into(senderimg)
                    }
                }

                val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.chat_message_reccieved_recycle, null)
                holder.messagecontent = view.findViewById(R.id.message_content)

            }
        }else if (message.tag == "video"){
            val params = holder.messagecontent.layoutParams
            params.width = 500 // replace with desired width in pixels
            params.height = 300 // replace with desired height in pixels
            holder.messagecontent.layoutParams = params
            val content = "Video\n" + message.content
            holder.messagecontent.text = content
            holder.messagetime.text = message.time

            if (message.senderid == mAuth.uid.toString()){
                val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.chat_message_sent_recycle, null)
                holder.messagecontent = view.findViewById(R.id.message_content)
            }
            else{
                message.senderpic.let { senderpic ->
                    holder.senderimg?.let { senderimg ->
                        Glide.with(holder.itemView)
                            .load(senderpic)
                            .into(senderimg)
                    }
                }

                val view = LayoutInflater.from(holder.itemView.context).inflate(R.layout.chat_message_reccieved_recycle, null)
                holder.messagecontent = view.findViewById(R.id.message_content)

            }
        }

    }
}
