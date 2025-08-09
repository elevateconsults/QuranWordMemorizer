package store.jaranation.quranwordmemorizer.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import store.jaranation.quranwordmemorizer.data.preferences.QuizPreferences
import store.jaranation.quranwordmemorizer.data.preferences.QuizSettings
import store.jaranation.quranwordmemorizer.notifications.QuizNotificationManager
import store.jaranation.quranwordmemorizer.quiz.QuizScheduler

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = QuizPreferences(application)
    private val notificationManager = QuizNotificationManager(application)
    val settings: Flow<QuizSettings> = preferences.quizSettings

    fun updateNotificationEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferences.updateNotificationEnabled(enabled)
            if (enabled) {
                QuizScheduler.scheduleQuizNotifications(getApplication())
            } else {
                QuizScheduler.cancelQuizNotifications(getApplication())
            }
        }
    }

    fun updateNotificationFrequency(minutes: Int) {
        viewModelScope.launch {
            preferences.updateNotificationFrequency(minutes)
            // Show immediate notification
            notificationManager.showQuizNotificationWithMessage(
                "Quiz notifications set to every ${formatFrequency(minutes)}",
                System.currentTimeMillis().toInt()
            )
            // Reschedule notifications with new frequency
            QuizScheduler.cancelQuizNotifications(getApplication())
            QuizScheduler.scheduleQuizNotifications(getApplication())
        }
    }

    private fun formatFrequency(minutes: Int): String {
        return when {
            minutes < 60 -> "$minutes minutes"
            minutes == 60 -> "1 hour"
            minutes % 60 == 0 -> "${minutes / 60} hours"
            else -> "${minutes / 60}h ${minutes % 60}m"
        }
    }

    fun updateQuizSource(source: String) {
        viewModelScope.launch {
            preferences.updateQuizSource(source)
        }
    }

    fun updateQuizDifficulty(difficulty: String) {
        viewModelScope.launch {
            preferences.updateQuizDifficulty(difficulty)
        }
    }

    fun updateTimeRange(startHour: Int, endHour: Int) {
        viewModelScope.launch {
            preferences.updateTimeRange(startHour, endHour)
        }
    }

    fun sendTestNotification() {
        notificationManager.showTestNotification()
    }

    fun updateTestMode(enabled: Boolean) {
        viewModelScope.launch {
            preferences.updateTestMode(enabled)
            // Cancel existing notifications and reschedule with new interval
            QuizScheduler.cancelQuizNotifications(getApplication())
            QuizScheduler.scheduleQuizNotifications(getApplication())
        }
    }
}