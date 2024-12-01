package store.jaranation.quranwordmemorizer.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Insert
    suspend fun insertWord(word: Word)

    @Insert
    suspend fun insertAll(words: List<Word>)

    @Update
    suspend fun updateWord(word: Word)

    @Delete
    suspend fun deleteWord(word: Word)

    @Query("SELECT * FROM word_bank ORDER BY arabic_word ASC")
    fun getAllWords(): Flow<List<Word>>

    @Query("SELECT * FROM word_bank WHERE source = :source ORDER BY arabic_word ASC")
    fun getWordsBySource(source: String): Flow<List<Word>>

    @Query("SELECT * FROM word_bank WHERE id = :wordId")
    suspend fun getWordById(wordId: Int): Word?

    @Query("SELECT COUNT(*) FROM word_bank WHERE source = 'quranic'")
    suspend fun getQuranicWordsCount(): Int

    @Query("SELECT COUNT(*) FROM word_bank WHERE source = 'user'")
    suspend fun getUserWordsCount(): Int

    @Query("SELECT COUNT(*) FROM word_bank")
    suspend fun getTotalWordsCount(): Int

    @Query("SELECT * FROM word_bank WHERE source = 'quranic' ORDER BY RANDOM() LIMIT :limit")
    suspend fun getUnquizzedWords(limit: Int): List<Word>

    @Query("SELECT * FROM word_bank WHERE id IN (:wordIds)")
    fun getWordsByIds(wordIds: List<Int>): Flow<List<Word>>

    @Query("SELECT * FROM word_bank WHERE id IN (:wordIds)")
    suspend fun getWordsByIdsSync(wordIds: List<Int>): List<Word>

    @Query("SELECT * FROM word_bank WHERE id IN (:wordIds)")
    suspend fun getWordsByIds(wordIds: Set<Int>): List<Word>
} 