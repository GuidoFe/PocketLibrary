package com.guidofe.pocketlibrary.notification

import android.app.PendingIntent
import android.app.PendingIntent.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.guidofe.pocketlibrary.MainActivity
import com.guidofe.pocketlibrary.R
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.utils.toModInt

class AlarmReceiver : BroadcastReceiver() {

    private var notificationManager: NotificationManagerCompat? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = (
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent?.getParcelableExtra("borrowed_bundle", BorrowedBundle::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent?.getParcelableExtra("borrowed_bundle")
            }
            )
        val tapResultIntent = Intent(context, MainActivity::class.java)
        tapResultIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        val pendingIntent: PendingIntent = getActivity(
            context, 0, tapResultIntent, FLAG_UPDATE_CURRENT or FLAG_IMMUTABLE
        )
        val notification = context?.let {
            NotificationCompat.Builder(it, "due_books")
                .setContentTitle(it.getString(R.string.notification_title_due))
                .setContentText(bundle?.bookBundle?.book?.title)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
        }
        notificationManager = context?.let { NotificationManagerCompat.from(it) }
        notification?.let { n ->
            bundle?.let { b ->
                notificationManager?.notify(b.info.bookId.toModInt(), n)
            }
        }
    }
}