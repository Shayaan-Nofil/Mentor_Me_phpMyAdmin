import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ShayaanNofil.i210450.R
import com.ShayaanNofil.i210450.homerecycle_adapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import de.hdodenhof.circleimageview.CircleImageView

class chat_recieve_recycle_adapter(private val items: MutableList<Messages>): RecyclerView.Adapter<chat_recieve_recycle_adapter.ViewHolder>() {
    private lateinit var onClickListener: chat_recieve_recycle_adapter.OnClickListener

    class ViewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        val senderimg: CircleImageView = itemView.findViewById(R.id.sender_img)
        val messagecontent: TextView = itemView.findViewById(R.id.message_content)
        val messagetime: TextView = itemView.findViewById(R.id.message_time)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): chat_recieve_recycle_adapter.ViewHolder {
        var onClickListener: OnClickListener? = null

        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message_reccieved_recycle, parent, false)
        return chat_recieve_recycle_adapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: chat_recieve_recycle_adapter.ViewHolder, position: Int) {
        Log.w("TAG", "In onbind holder")
        val message = items[position]

        holder.messagecontent.text = message.content
        holder.messagetime.text = message.time

        Glide.with(holder.itemView)
            .load(message.senderpic)
            .into(holder.senderimg)

//        holder.itemView.setOnClickListener {
//            onClickListener.onClick(position, mentor)
//        }
    }

    // A function to bind the onclickListener.
    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    // onClickListener Interface
    interface OnClickListener {
        fun onClick(position: Int, model: Messages)
    }

}