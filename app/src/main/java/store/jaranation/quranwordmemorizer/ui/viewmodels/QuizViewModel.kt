package store.jaranation.quranwordmemorizer.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import store.jaranation.quranwordmemorizer.data.local.Word
import store.jaranation.quranwordmemorizer.data.local.WordDatabase
import store.jaranation.quranwordmemorizer.data.repository.WordRepository

data class QuizState(
    val currentWord: Word? = null,
    val options: List<String> = emptyList(),
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val correctAnswers: Int = 0,
    val totalAttempts: Int = 0
)

class QuizViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WordRepository(WordDatabase.getDatabase(application).wordDao())
    private val words = mutableListOf<Word>()
    
    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()
    
    init {
        loadWords()
    }
    
    private fun loadWords() {
        viewModelScope.launch {
            try {
                words.clear()
                words.addAll(repository.allWords.first())
                if (words.isNotEmpty()) {
                    nextQuestion()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
    
    fun checkAnswer(selectedAnswer: String) {
        val currentState = _quizState.value
        val isCorrect = currentState.currentWord?.englishMeaning == selectedAnswer
        
        _quizState.value = currentState.copy(
            isAnswered = true,
            isCorrect = isCorrect,
            correctAnswers = currentState.correctAnswers + if (isCorrect) 1 else 0,
            totalAttempts = currentState.totalAttempts + 1
        )
    }
    
    fun nextQuestion() {
        if (words.isEmpty()) {
            return
        }
        
        val currentWord = words.random()
        val wrongOptions = words
            .filter { it != currentWord }
            .shuffled()
            .take(3)
            .map { it.englishMeaning }
        
        val options = (wrongOptions + currentWord.englishMeaning).shuffled()
        
        _quizState.value = _quizState.value.copy(
            currentWord = currentWord,
            options = options,
            isAnswered = false
        )
    }
} 