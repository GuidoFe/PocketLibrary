package com.guidofe.pocketlibrary.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.guidofe.pocketlibrary.data.local.library_db.BorrowedBundle
import com.guidofe.pocketlibrary.utils.toModInt
import java.time.Instant
import java.util.*

class AppNotificationManager(val context: Context) {

    companion object {
        const val DUE_BOOKS_CHANNEL_NAME = "due_books"
    }
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun createPendingIntent(id: Int, title: String): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("title", title)
        intent.putExtra("id", id)
        return PendingIntent.getBroadcast(
            context, id, intent, PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun setDueDateNotification(bundle: BorrowedBundle, timeMilli: Long) {
        val pendingIntent = createPendingIntent(
            bundle.info.bookId.toModInt(),
            bundle.bookBundle.book.title
        )
        Log.d(
            "notification",
            "Setting alarm with id ${bundle.info.bookId} for time " +
                "${Date.from(Instant.ofEpochMilli(timeMilli))}"
        )
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeMilli, pendingIntent)
    }

    fun deleteDueDateNotification(bookId: Long) {
        alarmManager.cancel(createPendingIntent(bookId.toModInt(), ""))
    }
}