package store.jaranation.quranwordmemorizer.quiz

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import store.jaranation.quranwordmemorizer.data.local.Word
import store.jaranation.quranwordmemorizer.ui.viewmodels.WordViewModel
import kotlin.random.Random

class QuizManager(private val wordViewModel: WordViewModel) {

    fun generateQuiz(quizSource: String): Flow<QuizQuestion> {
        return wordViewModel.getRandomWords(quizSource, 3).map { words ->
            if (words.isEmpty()) {
                throw IllegalStateException("No words available for quiz source: $quizSource")
            }
            
            // Select correct answer and generate question
            val correctWord = words[0]
            val questionType = QuestionType.values()[Random.nextInt(QuestionType.values().size)]
            
            when (questionType) {
                QuestionType.ARABIC_TO_ENGLISH -> QuizQuestion(
                    question = correctWord.arabicWord,
                    correctAnswer = correctWord.englishMeaning,
                    options = words.map { it.englishMeaning }.shuffled(),
                    type = questionType,
                    correctWord = correctWord,
                    allWords = words
                )
                QuestionType.ENGLISH_TO_ARABIC -> QuizQuestion(
                    question = correctWord.englishMeaning,
                    correctAnswer = correctWord.arabicWord,
                    options = words.map { it.arabicWord }.shuffled(),
                    type = questionType,
                    correctWord = correctWord,
                    allWords = words
                )
            }
        }
    }
}

data class QuizQuestion(
    val question: String,
    val correctAnswer: String,
    val options: List<String>,
    val type: QuestionType,
    val correctWord: Word,
    val allWords: List<Word>
)

enum class QuestionType {
    ARABIC_TO_ENGLISH,
    ENGLISH_TO_ARABIC
}