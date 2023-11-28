package my.edu.tarc.moneymate.Alarm

import android.app.Notification
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.moneymate.R

class AlarmNotificationAdapter(private var alarmNotification: List<AlarmNotification>,private val listener: AlarmNotificationClickListener) :
    RecyclerView.Adapter<AlarmNotificationAdapter.ViewHolder>() {

//    private val alarmNotificationViewModel: AlarmNotificationViewModel,
    interface AlarmNotificationClickListener {
        fun onEditClicked(alarm: AlarmNotification)
        fun onDeleteClicked(alarm: AlarmNotification)
    }
    class ViewHolder(itemView: View, private val listener: AlarmNotificationClickListener) : RecyclerView.ViewHolder(itemView) {
        fun bind(notification: AlarmNotification)
        {
            itemView.findViewById<TextView>(R.id.tvAlarmNotificationTitle).text = notification.title
            itemView.findViewById<TextView>(R.id.tvAlarmNotificationDesc).text = notification.description
            itemView.findViewById<TextView>(R.id.tvAlarmNotificationHour).text = String.format("%02d", notification.hour)
            itemView.findViewById<TextView>(R.id.tvAlarmNotificationMinit).text = String.format("%02d",notification.minit)

            itemView.setOnClickListener{
                listener.onEditClicked(notification)

            }
//            itemView.findViewById<ImageView>(R.id.button_delete).setOnClickListener {
//                listener.onDeleteClicked(notification)
//            }
        }
    }

    fun updateAlarm(newAlarmNotification: List<AlarmNotification>){
//        alarmNotificationViewModel.updateNotification(newAlarmNotification)
        alarmNotification = newAlarmNotification
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Implement this method to inflate your ViewHolder's layout
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_notification_layout, parent, false)
        return ViewHolder(view, listener)
    }

    override fun getItemCount(): Int {
        return alarmNotification.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(alarmNotification[position])
    }

//    private fun deleteItem(position: Int){
//       alarmNotificationViewModel.delete(alarmNotification[position])
//    }
}