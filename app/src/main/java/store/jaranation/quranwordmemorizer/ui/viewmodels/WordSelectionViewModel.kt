package store.jaranation.quranwordmemorizer.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import store.jaranation.quranwordmemorizer.data.local.WordDatabase
import store.jaranation.quranwordmemorizer.data.preferences.QuizPreferences
import store.jaranation.quranwordmemorizer.data.repository.WordRepository

class WordSelectionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WordRepository(WordDatabase.getDatabase(application).wordDao())
    private val preferences = QuizPreferences(application)
    
    val userWords = repository.getWordsBySource("user")
    
    private val _selectedWordIds = MutableStateFlow<Set<Int>>(emptySet())
    val selectedWords = _selectedWordIds.asStateFlow()
    
    private val _quizDuration = MutableStateFlow(7)
    val quizDuration = _quizDuration.asStateFlow()
    
    init {
        // Load saved selections
        viewModelScope.launch {
            preferences.quizSettings.collect { settings ->
                _selectedWordIds.value = settings.selectedWordIds
                _quizDuration.value = settings.quizDuration
            }
        }
    }
    
    fun selectWord(wordId: Int) {
        _selectedWordIds.value = _selectedWordIds.value + wordId
    }
    
    fun deselectWord(wordId: Int) {
        _selectedWordIds.value = _selectedWordIds.value - wordId
    }
    
    fun updateQuizDuration(days: Int) {
        _quizDuration.value = days
    }
    
    fun saveSelection() {
        viewModelScope.launch {
            preferences.updateSelectedWords(_selectedWordIds.value)
            preferences.updateQuizDuration(_quizDuration.value)
            // Automatically switch to selected words mode if words are selected
            if (_selectedWordIds.value.isNotEmpty()) {
                preferences.updateQuizSource("selected")
            }
        }
    }
} 