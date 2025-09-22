package store.jaranation.qwm2.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import store.jaranation.qwm2.util.NotificationUtils

class TestNotificationWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        NotificationUtils.showSimpleNotification(
            context = applicationContext,
            title = "QWM+ Reminder",
            text = "Time to practice a few words"
        )
        return Result.success()
    }
}
