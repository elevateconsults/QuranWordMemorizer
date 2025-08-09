package store.jaranation.quranwordmemorizer.quiz

import store.jaranation.quranwordmemorizer.data.local.Word
import store.jaranation.quranwordmemorizer.data.repository.WordRepository
import kotlin.random.Random

enum class QuizDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED
}

data class QuizQuestion(
    val question: Word,
    val options: List<String>,
    val correctAnswer: String,
    val questionType: QuestionType,
    val optionTransliterations: List<String>? = null
)

enum class QuestionType {
    ARABIC_TO_ENGLISH,    // Show Arabic, guess English
    ENGLISH_TO_ARABIC     // Show English, guess Arabic
}

class QuizManager(private val repository: WordRepository) {
    private val maxDailyWords = 5  // Start with just 5 words per day
    
    suspend fun getNextQuizWord(): Word? {
        // Get words that haven't been quizzed today
        val words = repository.getUnquizzedWords(maxDailyWords)
        return words.randomOrNull()
    }

    suspend fun generateQuestion(difficulty: QuizDifficulty): QuizQuestion? {
        val numWrongWords = when (difficulty) {
            QuizDifficulty.BEGINNER -> 1
            QuizDifficulty.INTERMEDIATE -> 2
            QuizDifficulty.ADVANCED -> 3
        }

        // We need at least one correct word and enough wrong words
        val totalWordsNeeded = numWrongWords + 1
        val availableWords = repository.getUnquizzedWords(totalWordsNeeded * 2) // Fetch extra to be safe

        if (availableWords.size < totalWordsNeeded) return null

        val correctWord = availableWords.first()
        val wrongWords = availableWords.drop(1).take(numWrongWords)

        // 80% chance of Arabic-to-English questions
        val questionType = if (Random.nextFloat() < 0.8f) {
            QuestionType.ARABIC_TO_ENGLISH
        } else {
            QuestionType.ENGLISH_TO_ARABIC
        }
            
        val optionsWithTransliterations = when (questionType) {
            QuestionType.ARABIC_TO_ENGLISH -> {
                val options = (wrongWords.map { it.englishMeaning } + correctWord.englishMeaning).shuffled()
                Pair(options, null) // No transliterations needed for English options
            }
            QuestionType.ENGLISH_TO_ARABIC -> {
                // For Arabic options, we need both the words and their transliterations
                val wordsWithTransliterations = (wrongWords + correctWord).shuffled()
                val options = wordsWithTransliterations.map { it.arabicWord }
                // Handle null transliterations by providing a default empty string
                val transliterations = wordsWithTransliterations.map { it.transliteration ?: "" }
                Pair(options, transliterations)
            }
        }

        return QuizQuestion(
            question = correctWord,
            options = optionsWithTransliterations.first,
            correctAnswer = if (questionType == QuestionType.ARABIC_TO_ENGLISH) 
                correctWord.englishMeaning else correctWord.arabicWord,
            questionType = questionType,
            optionTransliterations = optionsWithTransliterations.second
        )
    }

    private fun getAnswerBasedOnType(word: Word, type: QuestionType): String {
        return when (type) {
            QuestionType.ARABIC_TO_ENGLISH -> word.englishMeaning
            QuestionType.ENGLISH_TO_ARABIC -> word.arabicWord
        }
    }
}