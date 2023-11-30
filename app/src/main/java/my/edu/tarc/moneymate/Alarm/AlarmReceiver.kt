package my.edu.tarc.moneymate.Alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import my.edu.tarc.moneymate.MainActivity
import my.edu.tarc.moneymate.R

class AlarmReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        //ensure context is not null
        context?:return

        //notification cahnnel ID and Name
//        val channelId = "Alarm Notification Channel"
        val channelName = " Alarm Notification Channel name"
        val channelId = intent?.getStringExtra("Channel_ID")
        var title = intent?.getStringExtra("Title")
        if (title == "")
        {
            title = "Alarm Notification Title"
        }
        var desc = intent?.getStringExtra("Desc")
        if (desc == "")
        {
            desc = "Alarm Notification Description"
        }
        Log.d("channel ID", channelId.toString())
        // create a notification manager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //create a notification channel
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)


        val pendingIntent = PendingIntent.getActivities(
            context,
            0,
            arrayOf(Intent(context,MainActivity::class.java)),
          //  PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_IMMUTABLE
           PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        //create the notification
        val notification = NotificationCompat.Builder(context, channelId.toString())
            .setContentTitle(title.toString())
            .setContentText(desc.toString())
            .setSmallIcon(R.drawable.round_check_24)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0,notification)

        Log.d("AlarmReceiver", "Received alarm trigger event")

    }

}