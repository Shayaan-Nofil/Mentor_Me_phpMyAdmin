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

class booked_session_recycle_adapter(private val items: MutableList<Sessions>): RecyclerView.Adapter<booked_session_recycle_adapter.ViewHolder>() {

    class ViewHolder (itemview: View): RecyclerView.ViewHolder(itemview){
        val mentorimg: ImageView = itemView.findViewById(R.id.mentor_img)
        val mentorname: TextView = itemView.findViewById(R.id.mentor_name)
        val sessiontime: TextView = itemView.findViewById(R.id.session_time)
        val mentorjob: TextView = itemView.findViewById(R.id.mentor_job)
        val sessiondate: TextView = itemView.findViewById(R.id.session_date)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): booked_session_recycle_adapter.ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.bookedsession_recycle, parent, false)
        return booked_session_recycle_adapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }
    override fun onBindViewHolder(holder: booked_session_recycle_adapter.ViewHolder, position: Int) {
        Log.w("TAG", "In onbind holder")
        val session = items[position]

        holder.mentorname.text = session.mentorname
        holder.mentorjob.text = session.mentorjob
        holder.sessiondate.text = session.date
        holder.sessiontime.text = session.time

        var rqoptions: RequestOptions = RequestOptions()
        rqoptions= rqoptions.transform(CenterCrop(), RoundedCorners(40))
        Glide.with(holder.itemView)
            .load(session.mentorimg)
            .apply(rqoptions)
            .into(holder.mentorimg)
    }

}