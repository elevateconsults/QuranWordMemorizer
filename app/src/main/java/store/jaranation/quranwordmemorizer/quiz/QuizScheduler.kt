package store.jaranation.quranwordmemorizer.quiz

import android.content.Context
import androidx.work.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import store.jaranation.quranwordmemorizer.data.preferences.QuizPreferences
import java.util.concurrent.TimeUnit
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class QuizScheduler {
    companion object {
        private const val QUIZ_WORK_NAME = "quiz_notification_work"
        private const val TEST_MODE_INTERVAL = 2L // 2 minutes for testing
        private const val MIN_PERIODIC_INTERVAL = 15L // WorkManager minimum interval

        fun scheduleQuizNotifications(context: Context) {
            val preferences = QuizPreferences(context)
            
            // Get current settings
            val settings = runBlocking { preferences.quizSettings.first() }
            
            if (!settings.isNotificationEnabled) {
                cancelQuizNotifications(context)
                return
            }

            // Use test mode interval if enabled
            val requestedInterval = if (settings.isTestMode) {
                TEST_MODE_INTERVAL
            } else {
                settings.notificationFrequency.toLong()
            }

            // Cancel any existing work
            WorkManager.getInstance(context).cancelAllWorkByTag(QUIZ_WORK_NAME)

            val workRequest = if (requestedInterval < MIN_PERIODIC_INTERVAL) {
                // For intervals less than 15 minutes, use OneTimeWorkRequest with chain
                val initialDelay = requestedInterval
                OneTimeWorkRequestBuilder<QuizWorker>()
                    .setInitialDelay(initialDelay, TimeUnit.MINUTES)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                            .build()
                    )
                    .addTag(QUIZ_WORK_NAME)
                    .build()
            } else {
                // For intervals 15 minutes or greater, use PeriodicWorkRequest
                PeriodicWorkRequestBuilder<QuizWorker>(
                    requestedInterval, TimeUnit.MINUTES,
                    (requestedInterval * 0.1).toLong(), TimeUnit.MINUTES  // 10% flex interval
                )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                        .build()
                )
                .addTag(QUIZ_WORK_NAME)
                .build()
            }

            if (requestedInterval < MIN_PERIODIC_INTERVAL) {
                // For short intervals, enqueue as OneTimeWork
                WorkManager.getInstance(context)
                    .enqueue(workRequest)
                    
                // Schedule the next work after this one completes
                WorkManager.getInstance(context)
                    .getWorkInfoByIdLiveData(workRequest.id)
                    .observeForever { workInfo ->
                        if (workInfo?.state == WorkInfo.State.SUCCEEDED) {
                            // Schedule next notification
                            scheduleQuizNotifications(context)
                        }
                    }
            } else {
                // For longer intervals, enqueue as PeriodicWork
                WorkManager.getInstance(context)
                    .enqueueUniquePeriodicWork(
                        QUIZ_WORK_NAME,
                        ExistingPeriodicWorkPolicy.UPDATE,
                        workRequest as PeriodicWorkRequest
                    )
            }
        }

        fun cancelQuizNotifications(context: Context) {
            WorkManager.getInstance(context).cancelAllWorkByTag(QUIZ_WORK_NAME)
        }
    }
}