package com.guidofe.pocketlibrary.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.utils.toModInt

class NotificationManager(val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun createPendingIntent(id: Long): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, id.toModInt(), intent, PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun setDueDateNotification(bundle: BorrowedBundle, time: Long) {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("borrowed_bundle", bundle)
        val pendingIntent = createPendingIntent(bundle.info.bookId)
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }

    fun deleteDueDateNotification(bookId: Long) {
        alarmManager.cancel(createPendingIntent(bookId))
    }
}