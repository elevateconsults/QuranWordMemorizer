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
import store.jaranation.quranwordmemorizer.ui.viewmodels.WordViewModel
import store.jaranation.quranwordmemorizer.ui.viewmodels.WordViewModelFactory
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
            val factory = WordViewModelFactory(repository)
            val wordViewModel = factory.create(WordViewModel::class.java)
            wordViewModel.initialize()
            val quizManager = QuizManager(wordViewModel)

            // Generate quiz based on selected source
            val question = quizManager.generateQuiz(settings.quizSource).first()

            // Show notification with the question
            val notificationManager = QuizNotificationManager(context)
            notificationManager.showQuizNotification(question, System.currentTimeMillis().toInt())

            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}