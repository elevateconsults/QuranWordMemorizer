package store.jaranation.qwm2.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import store.jaranation.qwm2.R
import store.jaranation.qwm2.work.TestNotificationWorker

object NotificationUtils {
    const val CHANNEL_ID = "qwmplus_reminders"

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Practice Reminders"
            val descriptionText = "Reminders to practice Quran vocabulary"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showSimpleNotification(context: Context, title: String, text: String, id: Int = 1001) {
        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_small)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
    }

    fun enqueueTestNotification(context: Context) {
        val req = OneTimeWorkRequestBuilder<TestNotificationWorker>().build()
        WorkManager.getInstance(context).enqueue(req)
    }
}
