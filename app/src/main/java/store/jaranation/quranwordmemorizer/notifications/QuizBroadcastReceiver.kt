package store.jaranation.quranwordmemorizer.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import store.jaranation.quranwordmemorizer.quiz.QuizScheduler

class QuizBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        when (intent.action) {
            "CORRECT_ANSWER", "WRONG_ANSWER" -> {
                // Get the notification ID and correct answer
                val notificationId = intent.getIntExtra("notification_id", 0)
                val correctAnswer = intent.getStringExtra("correct_answer") ?: return
                
                // Cancel the question notification immediately
                notificationManager.cancel(notificationId)

                // Determine if answer was correct based on the action
                val isCorrect = intent.action == "CORRECT_ANSWER"

                // Show feedback using QuizNotificationManager
                val quizNotificationManager = QuizNotificationManager(context)
                quizNotificationManager.showFeedbackNotification(
                    isCorrect = isCorrect,
                    answer = correctAnswer,
                    notificationId = notificationId
                )

                // Show immediate feedback with Toast
                val message = if (isCorrect) "Correct! ✓" else "Incorrect. The correct answer was: $correctAnswer"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()

                // Schedule next quiz if it was correct and not a test
                if (isCorrect && !intent.getBooleanExtra("isTest", false)) {
                    QuizScheduler.scheduleQuizNotifications(context)
                }
            }

            "NEXT_QUESTION" -> {
                // Show a new question
                val quizNotificationManager = QuizNotificationManager(context)
                quizNotificationManager.showTestNotification()
            }
        }
    }
}