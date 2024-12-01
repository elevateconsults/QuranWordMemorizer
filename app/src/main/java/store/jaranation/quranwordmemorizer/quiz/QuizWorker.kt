package store.jaranation.quranwordmemorizer.quiz

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import store.jaranation.quranwordmemorizer.data.local.WordDatabase
import store.jaranation.quranwordmemorizer.notifications.QuizNotificationManager
import store.jaranation.quranwordmemorizer.data.repository.WordRepository
import store.jaranation.quranwordmemorizer.data.preferences.QuizPreferences
import java.time.LocalTime

class QuizWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val preferences = QuizPreferences(context)
            val settings = preferences.quizSettings.first()
            
            // Check if we're within active hours
            val currentTime = LocalTime.now()
            val startTime = LocalTime.of(settings.startTime, 0)
            val endTime = LocalTime.of(settings.endTime, 0)
            
            if (currentTime.isBefore(startTime) || currentTime.isAfter(endTime)) {
                return@withContext Result.success()
            }

            val wordDao = WordDatabase.getDatabase(context).wordDao()
            val repository = WordRepository(wordDao)
            
            // Get words based on quiz source setting
            val words = when (settings.quizSource) {
                "quranic" -> repository.getWordsBySource("quranic").first()
                "user" -> repository.getWordsBySource("user").first()
                "selected" -> repository.getWordsByIds(settings.selectedWordIds)
                else -> repository.allWords.first()
            }

            if (words.isEmpty()) {
                return@withContext Result.retry()
            }

            val quizManager = QuizManager(repository)
            val question = quizManager.generateQuestion()

            if (question != null) {
                val quizNotificationManager = QuizNotificationManager(context)
                withContext(Dispatchers.Main) {
                    quizNotificationManager.showQuizNotification(
                        question,
                        System.currentTimeMillis().toInt()
                    )
                }
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}