package store.jaranation.quranwordmemorizer.notifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import store.jaranation.quranwordmemorizer.quiz.QuizQuestion
import store.jaranation.quranwordmemorizer.quiz.QuestionType
import store.jaranation.quranwordmemorizer.data.local.Word
import store.jaranation.quranwordmemorizer.data.local.WordDatabase
import store.jaranation.quranwordmemorizer.data.repository.WordRepository
import store.jaranation.quranwordmemorizer.quiz.QuizManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import store.jaranation.quranwordmemorizer.ui.viewmodels.WordViewModel
import store.jaranation.quranwordmemorizer.ui.viewmodels.WordViewModelFactory

class QuizNotificationManager(private val context: Context) {
    private val channelId = "quiz_channel"
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        createNotificationChannel()
    }

    fun showTestNotification() {
        scope.launch {
            try {
                val wordDao = WordDatabase.getDatabase(context).wordDao()
                val repository = WordRepository(wordDao)
                val factory = WordViewModelFactory(repository)
                val wordViewModel = factory.create(WordViewModel::class.java)
                wordViewModel.initialize()
                val quizManager = QuizManager(wordViewModel)
                
                val question = quizManager.generateQuiz("both").first()
                showQuizNotification(question, System.currentTimeMillis().toInt())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showQuizNotification(question: QuizQuestion, notificationId: Int) {
        val collapsedText = getCollapsedQuestionText(question)
        val expandedText = getExpandedQuizText(question)

        // Create a notification channel with longer display time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(channelId)
            if (channel != null) {
                channel.enableLights(true)
                channel.enableVibration(true)
                channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                notificationManager.createNotificationChannel(channel)
            }
        }

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Quran Word Quiz")
            .setContentText(collapsedText)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(expandedText)
                .setSummaryText("Select your answer below"))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)  // Makes notification persistent until answered
            .setAutoCancel(false)  // Prevents auto-cancellation
            .setTimeoutAfter(-1)  // No timeout
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOnlyAlertOnce(false)  // Allow multiple alerts
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setFullScreenIntent(null, true)  // Makes notification heads-up
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))  // Extended vibration pattern

        // Add action buttons for each option with better spacing
        question.options.forEachIndexed { index, option ->
            val letter = ('A' + index).toString()
            val isCorrectAnswer = option == question.correctAnswer
            val intent = Intent(context, QuizBroadcastReceiver::class.java).apply {
                action = if (isCorrectAnswer) "CORRECT_ANSWER" else "WRONG_ANSWER"
                putExtra("notification_id", notificationId)
                putExtra("correct_answer", question.correctAnswer)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                index + (notificationId * 10),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Format the button text without transliteration
            val buttonText = "$letter. $option"
            
            notificationBuilder.addAction(
                NotificationCompat.Action.Builder(
                    0,
                    buttonText,
                    pendingIntent
                ).build()
            )
        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    fun showQuizNotificationWithMessage(message: String, notificationId: Int) {
        // Create a notification channel with longer display time
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = notificationManager.getNotificationChannel(channelId)
            if (channel != null) {
                channel.enableLights(true)
                channel.enableVibration(true)
                channel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                notificationManager.createNotificationChannel(channel)
            }
        }

        // Show a confirmation notification first
        val confirmBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Quiz Frequency Updated")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setTimeoutAfter(5000) // 5 seconds for confirmation message

        notificationManager.notify(notificationId, confirmBuilder.build())

        // Show an actual quiz notification after a short delay
        scope.launch {
            try {
                delay(5000) // Wait 5 seconds instead of 1
                val wordDao = WordDatabase.getDatabase(context).wordDao()
                val repository = WordRepository(wordDao)
                val factory = WordViewModelFactory(repository)
                val wordViewModel = factory.create(WordViewModel::class.java)
                wordViewModel.initialize()
                val quizManager = QuizManager(wordViewModel)
                
                val question = quizManager.generateQuiz("both").first()
                showQuizNotification(question, notificationId + 1)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getCollapsedQuestionText(question: QuizQuestion): String {
        return "What is the ${if (question.type == QuestionType.ARABIC_TO_ENGLISH) "English meaning" else "Arabic word"} for '${question.question}'?"
    }

    private fun getExpandedQuizText(question: QuizQuestion): String {
        val questionText = when (question.type) {
            QuestionType.ARABIC_TO_ENGLISH -> {
                // For Arabic question, include transliteration
                val transliteration = question.correctWord.transliteration
                "What is the English meaning for '${question.question}' (${transliteration ?: ""})?"
            }
            QuestionType.ENGLISH_TO_ARABIC -> {
                "What is the Arabic word for '${question.question}'?"
            }
        }
        
        val optionsText = question.options.mapIndexed { index, option ->
            val letter = ('A' + index)
            when (question.type) {
                QuestionType.ENGLISH_TO_ARABIC -> {
                    // For Arabic options, include only transliteration
                    val word = if (option == question.correctWord.arabicWord) {
                        question.correctWord
                    } else {
                        // Find the word from the original list that matches this option
                        question.allWords.find { it.arabicWord == option }
                    }
                    val transliteration = word?.transliteration ?: ""
                    "$letter. $option (${transliteration})"
                }
                QuestionType.ARABIC_TO_ENGLISH -> {
                    "$letter. $option"
                }
            }
        }.joinToString("\n")
        
        return "$questionText\n\n$optionsText"
    }

    fun showFeedbackNotification(isCorrect: Boolean, answer: String, notificationId: Int) {
        val title = if (isCorrect) "Correct!" else "Incorrect"
        val message = if (isCorrect) 
            "Great job! You got it right!" 
        else 
            "The correct answer was: $answer"

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(true)
            .setTimeoutAfter(180000) // 3 minutes for feedback notification

        notificationManager.notify(notificationId + 1000, notificationBuilder.build())
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Quiz Channel"
            val descriptionText = "Channel for Quran Word Quiz notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                setShowBadge(true)
                enableLights(true)
                enableVibration(true)
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }
}