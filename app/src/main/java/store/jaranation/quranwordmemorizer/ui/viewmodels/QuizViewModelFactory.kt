package store.jaranation.quranwordmemorizer.ui.viewmodels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import store.jaranation.quranwordmemorizer.data.local.WordDatabase
import store.jaranation.quranwordmemorizer.data.preferences.QuizPreferences
import store.jaranation.quranwordmemorizer.data.repository.WordRepository
import store.jaranation.quranwordmemorizer.quiz.QuizManager

class QuizViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            val database = WordDatabase.getDatabase(application)
            val repository = WordRepository(database.wordDao())
            val quizManager = QuizManager(repository)
            val quizPreferences = QuizPreferences(application)
            @Suppress("UNCHECKED_CAST")
            return QuizViewModel(quizManager, quizPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
