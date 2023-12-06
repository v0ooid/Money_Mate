package my.edu.tarc.moneymate.Alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import my.edu.tarc.moneymate.MainActivity
import my.edu.tarc.moneymate.R

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        const val ACTION_GOAL_REMINDER = "my.edu.tarc.moneymate.ACTION_GOAL_REMINDER"
    }
    override fun onReceive(context: Context?, intent: Intent) {
        context ?: return
        Log.d("Alarm Receiver","Received what intent $intent")
        if (intent.action == AlarmReceiver.ACTION_GOAL_REMINDER) {
            // Handle the test alarm
            Log.d("AlarmReceiver", "Test alarm received")
            // Optionally create a notification or any other test action here
        }
        when (intent?.action) {
            ACTION_GOAL_REMINDER -> intent?.let { handleGoalReminder(context, it) }
            else -> intent?.let { handleDefaultAlarm(context, it) }
        }
    }


    private fun handleGoalReminder(context: Context, intent: Intent) {
        val goalId = intent.getLongExtra("goalId", 0)
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        Log.d("AlarmReceiver", "Handling goal reminder")
        // Create a notification channel for goal reminders (if it hasn't been created already)
        val channelId = "Goal_Reminder_Channel_ID"

            val channelName = "Goal Reminder Notifications"
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)


        // Create an Intent to open the app (or a specific activity) when the notification is tapped
        val notificationIntent = Intent(context, MainActivity::class.java) // Replace MainActivity with the activity you want to open
        val pendingIntent = PendingIntent.getActivity(context, goalId.toInt(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Customize the notification's title and text
        val title = "Goal Reminder"
        val message = "Don't forget to check your goal progress!"

        // Build the notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.round_check_24) // Replace with your notification icon
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // Show the notification
        notificationManager.notify(goalId.toInt(), notification) // Use goalId as the notification ID for uniqueness
    }
    private fun handleDefaultAlarm(context: Context, intent: Intent) {
        val channelId = "Alarm_Notification_Channel_ID"
        val title = intent?.getStringExtra("Title") ?: "Alarm Notification"
        val desc = intent?.getStringExtra("Desc") ?: "Time to wake up!"
        val alarmId = intent?.getStringExtra("Alarm Id")?: 0
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(channelId, "Alarm Notification Channel", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.createNotificationChannel(channel)

        val notificationIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, alarmId.toString().toInt(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(desc)
            .setSmallIcon(R.drawable.round_check_24)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification)
    }

}
