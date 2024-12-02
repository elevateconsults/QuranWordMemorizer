package store.jaranation.quranwordmemorizer.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "quiz_settings")

class QuizPreferences(private val context: Context) {
    
    private object PreferencesKeys {
        val NOTIFICATION_ENABLED = booleanPreferencesKey("notification_enabled")
        val NOTIFICATION_FREQUENCY = intPreferencesKey("notification_frequency")
        val QUIZ_SOURCE = stringPreferencesKey("quiz_source")
        val START_TIME = intPreferencesKey("start_time")
        val END_TIME = intPreferencesKey("end_time")
        val SELECTED_WORD_IDS = stringPreferencesKey("selected_word_ids")
        val QUIZ_DURATION = intPreferencesKey("quiz_duration")
        val TEST_MODE = booleanPreferencesKey("test_mode")
    }

    val quizSettings: Flow<QuizSettings> = context.dataStore.data.map { preferences ->
        QuizSettings(
            isNotificationEnabled = preferences[PreferencesKeys.NOTIFICATION_ENABLED] ?: true,
            notificationFrequency = preferences[PreferencesKeys.NOTIFICATION_FREQUENCY] ?: 4,
            quizSource = preferences[PreferencesKeys.QUIZ_SOURCE] ?: "user",
            startTime = preferences[PreferencesKeys.START_TIME] ?: 8,
            endTime = preferences[PreferencesKeys.END_TIME] ?: 22,
            selectedWordIds = preferences[PreferencesKeys.SELECTED_WORD_IDS]?.split(",")
                ?.mapNotNull { it.toIntOrNull() }
                ?.toSet() ?: emptySet(),
            quizDuration = preferences[PreferencesKeys.QUIZ_DURATION] ?: 7,
            isTestMode = preferences[PreferencesKeys.TEST_MODE] ?: false
        )
    }

    suspend fun updateNotificationEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_ENABLED] = enabled
        }
    }

    suspend fun updateNotificationFrequency(hours: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NOTIFICATION_FREQUENCY] = hours
        }
    }

    suspend fun updateQuizSource(source: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.QUIZ_SOURCE] = source
        }
    }

    suspend fun updateTimeRange(startHour: Int, endHour: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.START_TIME] = startHour
            preferences[PreferencesKeys.END_TIME] = endHour
        }
    }

    suspend fun updateSelectedWords(wordIds: Set<Int>) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SELECTED_WORD_IDS] = wordIds.joinToString(",")
        }
    }

    suspend fun updateQuizDuration(days: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.QUIZ_DURATION] = days
        }
    }

    suspend fun updateTestMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TEST_MODE] = enabled
        }
    }
}