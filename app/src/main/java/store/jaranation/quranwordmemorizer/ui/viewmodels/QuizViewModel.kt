package store.jaranation.quranwordmemorizer.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import store.jaranation.quranwordmemorizer.data.preferences.QuizPreferences
import store.jaranation.quranwordmemorizer.data.preferences.QuizPreferences
import store.jaranation.quranwordmemorizer.quiz.QuizDifficulty
import store.jaranation.quranwordmemorizer.quiz.QuizManager
import store.jaranation.quranwordmemorizer.quiz.QuizQuestion
import store.jaranation.quranwordmemorizer.quiz.QuestionType

data class QuizState(
    val currentQuestion: QuizQuestion? = null,
    val questionText: String = "",
    val isAnswered: Boolean = false,
    val isCorrect: Boolean = false,
    val correctAnswers: Int = 0,
    val totalAttempts: Int = 0,
    val isLoading: Boolean = true
)

class QuizViewModel(
    private val quizManager: QuizManager,
    private val quizPreferences: QuizPreferences
) : ViewModel() {

    private val _quizState = MutableStateFlow(QuizState())
    val quizState: StateFlow<QuizState> = _quizState.asStateFlow()

    init {
        loadNextQuestion()
    }

    fun checkAnswer(selectedAnswer: String) {
        val currentState = _quizState.value
        val isCorrect = currentState.currentQuestion?.correctAnswer == selectedAnswer

        _quizState.value = currentState.copy(
            isAnswered = true,
            isCorrect = isCorrect,
            correctAnswers = currentState.correctAnswers + if (isCorrect) 1 else 0,
            totalAttempts = currentState.totalAttempts + 1
        )
    }

    fun loadNextQuestion() {
        viewModelScope.launch {
            _quizState.value = _quizState.value.copy(isLoading = true)
            val settings = quizPreferences.quizSettings.first()
            val difficulty = QuizDifficulty.valueOf(settings.quizDifficulty)
            val question = quizManager.generateQuestion(difficulty)

            val questionText = when (question?.questionType) {
                QuestionType.ARABIC_TO_ENGLISH -> question.question.arabicWord
                QuestionType.ENGLISH_TO_ARABIC -> question.question.englishMeaning
                null -> ""
            }

            _quizState.value = _quizState.value.copy(
                currentQuestion = question,
                questionText = questionText,
                isAnswered = false,
                isLoading = false
            )
        }
    }
} 