package store.jaranation.qwm2.data

import kotlinx.coroutines.flow.Flow

class WordRepository(private val dao: WordDao) {
    fun observeAll(): Flow<List<Word>> = dao.observeAll()
    fun observeCount(): Flow<Int> = dao.observeCount()

    suspend fun add(word: Word) = dao.insert(word)
    suspend fun update(word: Word) = dao.update(word)
    suspend fun delete(word: Word) = dao.delete(word)
    suspend fun clear() = dao.clear()
}
