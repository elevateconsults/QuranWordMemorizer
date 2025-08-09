package store.jaranation.quranwordmemorizer.quiz

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import store.jaranation.quranwordmemorizer.data.local.Word
import store.jaranation.quranwordmemorizer.data.local.WordDao
import store.jaranation.quranwordmemorizer.data.repository.WordRepository

class QuizManagerTest {

    private lateinit var quizManager: QuizManager
    private lateinit var fakeWordDao: FakeWordDao
    private lateinit var wordRepository: WordRepository

    // Helper class to fake the DAO
    class FakeWordDao : WordDao {
        private val words = mutableListOf<Word>()

        fun setWords(wordList: List<Word>) {
            words.clear()
            words.addAll(wordList)
        }

        override suspend fun getUnquizzedWords(limit: Int): List<Word> {
            return words.shuffled().take(limit)
        }

        // Unused methods for this test
        override suspend fun insertWord(word: Word) {}
        override suspend fun insertAll(words: List<Word>) {}
        override suspend fun updateWord(word: Word) {}
        override suspend fun deleteWord(word: Word) {}
        override fun getAllWords(): Flow<List<Word>> = flowOf(words)
        override fun getWordsBySource(source: String): Flow<List<Word>> = flowOf(words.filter { it.source == source })
        override suspend fun getWordById(wordId: Int): Word? = words.find { it.id == wordId }
        override suspend fun getQuranicWordsCount(): Int = words.count { it.source == "quranic" }
        override suspend fun getUserWordsCount(): Int = words.count { it.source == "user" }
        override suspend fun getTotalWordsCount(): Int = words.size
        override fun getWordsByIds(wordIds: List<Int>): Flow<List<Word>> = flowOf(words.filter { it.id in wordIds })
        override suspend fun getWordsByIdsSync(wordIds: List<Int>): List<Word> = words.filter { it.id in wordIds }
        override suspend fun getWordsByIds(wordIds: Set<Int>): List<Word> = words.filter { it.id in wordIds }
    }

    @Before
    fun setup() {
        fakeWordDao = FakeWordDao()
        wordRepository = WordRepository(fakeWordDao)
        quizManager = QuizManager(wordRepository)
    }

    @Test
    fun `generateQuestion returns null when not enough words are available`() = runBlocking {
        // Given the repository has only one word
        val word1 = Word(id = 1, arabicWord = "تفاح", englishMeaning = "Apple")
        fakeWordDao.setWords(listOf(word1))

        // When generating a question for any difficulty
        val question = quizManager.generateQuestion(QuizDifficulty.INTERMEDIATE)

        // Then the question should be null because we need at least 3 words for intermediate
        assertEquals(null, question)
    }

    @Test
    fun `generateQuestion with Intermediate difficulty returns 3 options`() = runBlocking {
        // Given the repository has enough words
        val words = listOf(
            Word(id = 1, arabicWord = "تفاح", englishMeaning = "Apple", transliteration = "Tuffah"),
            Word(id = 2, arabicWord = "موز", englishMeaning = "Banana", transliteration = "Mawz"),
            Word(id = 3, arabicWord = "برتقال", englishMeaning = "Orange", transliteration = "Burtuqal"),
            Word(id = 4, arabicWord = "عنب", englishMeaning = "Grape", transliteration = "Inab")
        )
        fakeWordDao.setWords(words)

        // When generating a question
        val question = quizManager.generateQuestion(QuizDifficulty.INTERMEDIATE)

        // Then the question should be valid
        assertNotNull(question)
        question!!

        // It should have 3 options (1 correct + 2 wrong)
        assertEquals(3, question.options.size)
        assertTrue(question.options.contains(question.correctAnswer))
    }

    @Test
    fun `generateQuestion with Beginner difficulty returns 2 options`() = runBlocking {
        // Given the repository has enough words
        val words = listOf(
            Word(id = 1, arabicWord = "تفاح", englishMeaning = "Apple", transliteration = "Tuffah"),
            Word(id = 2, arabicWord = "موز", englishMeaning = "Banana", transliteration = "Mawz"),
            Word(id = 3, arabicWord = "برتقال", englishMeaning = "Orange", transliteration = "Burtuqal")
        )
        fakeWordDao.setWords(words)

        // When generating a question
        val question = quizManager.generateQuestion(QuizDifficulty.BEGINNER)

        // Then the question should be valid
        assertNotNull(question)
        question!!

        // It should have 2 options (1 correct + 1 wrong)
        assertEquals(2, question.options.size)
        assertTrue(question.options.contains(question.correctAnswer))
    }

    @Test
    fun `generateQuestion with Advanced difficulty returns 4 options`() = runBlocking {
        // Given the repository has enough words
        val words = listOf(
            Word(id = 1, arabicWord = "تفاح", englishMeaning = "Apple", transliteration = "Tuffah"),
            Word(id = 2, arabicWord = "موز", englishMeaning = "Banana", transliteration = "Mawz"),
            Word(id = 3, arabicWord = "برتقال", englishMeaning = "Orange", transliteration = "Burtuqal"),
            Word(id = 4, arabicWord = "عنب", englishMeaning = "Grape", transliteration = "Inab")
        )
        fakeWordDao.setWords(words)

        // When generating a question
        val question = quizManager.generateQuestion(QuizDifficulty.ADVANCED)

        // Then the question should be valid
        assertNotNull(question)
        question!!

        // It should have 4 options (1 correct + 3 wrong)
        assertEquals(4, question.options.size)
        assertTrue(question.options.contains(question.correctAnswer))
    }
}
