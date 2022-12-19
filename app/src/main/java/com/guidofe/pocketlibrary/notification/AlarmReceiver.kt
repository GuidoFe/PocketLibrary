package com.guidofe.pocketlibrary.notification

import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.guidofe.pocketlibrary.MainActivity
import com.guidofe.pocketlibrary.R

class AlarmReceiver : BroadcastReceiver() {

    private var notificationManager: NotificationManagerCompat? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("notification", "Received intent")
        val title = intent?.getStringExtra("title")
        val id = intent?.getIntExtra("id", 0) ?: 0
        val tapResultIntent = Intent(context, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent: PendingIntent = getActivity(
            context, 0, tapResultIntent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
        val notification = context?.let {
            NotificationCompat.Builder(it, AppNotificationManager.DUE_BOOKS_CHANNEL_NAME)
                .setContentTitle(it.getString(R.string.notification_title_due))
                .setContentText(title ?: "ERROR title not valid")
                .setSmallIcon(R.drawable.menu_book_24px)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
        }
        notificationManager = context?.let { NotificationManagerCompat.from(it) }
        notification?.let { n ->
            notificationManager?.notify(id, n)
        }
    }
}