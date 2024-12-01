package store.jaranation.quranwordmemorizer.data.repository

import store.jaranation.quranwordmemorizer.data.local.Word
import store.jaranation.quranwordmemorizer.data.local.WordDao
import kotlinx.coroutines.flow.Flow

class WordRepository(private val wordDao: WordDao) {
    val allWords: Flow<List<Word>> = wordDao.getAllWords()
    
    fun getWordsBySource(source: String): Flow<List<Word>> =
        wordDao.getWordsBySource(source)

    suspend fun getWordsByIds(wordIds: Set<Int>): List<Word> =
        wordDao.getWordsByIdsSync(wordIds.toList())

    suspend fun insert(word: Word) {
        wordDao.insertWord(word)
    }

    suspend fun update(word: Word) {
        wordDao.updateWord(word)
    }

    suspend fun delete(word: Word) {
        wordDao.deleteWord(word)
    }
    
    suspend fun getWordById(id: Int): Word? {
        return wordDao.getWordById(id)
    }

    suspend fun getQuranicWordsCount(): Int = wordDao.getQuranicWordsCount()
    
    suspend fun getUserWordsCount(): Int = wordDao.getUserWordsCount()
    
    suspend fun getTotalWordsCount(): Int = wordDao.getTotalWordsCount()

    suspend fun getUnquizzedWords(limit: Int): List<Word> {
        return wordDao.getUnquizzedWords(limit)
    }
} 