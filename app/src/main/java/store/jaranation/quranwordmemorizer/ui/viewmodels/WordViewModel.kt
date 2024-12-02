package store.jaranation.quranwordmemorizer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import store.jaranation.quranwordmemorizer.data.local.Word
import store.jaranation.quranwordmemorizer.data.repository.WordRepository

class WordViewModel : ViewModel() {
    lateinit var repository: WordRepository
    
    private val _userWords = MutableStateFlow<List<Word>>(emptyList())
    val userWords: StateFlow<List<Word>> = _userWords.asStateFlow()

    private val _quranicWords = MutableStateFlow<List<Word>>(emptyList())
    val quranicWords: StateFlow<List<Word>> = _quranicWords.asStateFlow()

    fun initialize() {
        if (!::repository.isInitialized) return
        
        viewModelScope.launch {
            repository.getWordsBySource("user").collect { words ->
                _userWords.value = words
            }
        }
        viewModelScope.launch {
            repository.getWordsBySource("quranic").collect { words ->
                _quranicWords.value = words
            }
        }
    }

    fun addWord(word: Word) {
        if (!::repository.isInitialized) return
        viewModelScope.launch {
            repository.insert(word)
        }
    }

    fun deleteWord(word: Word) {
        if (!::repository.isInitialized) return
        viewModelScope.launch {
            repository.delete(word)
        }
    }

    fun getRandomWords(source: String, limit: Int): Flow<List<Word>> {
        if (!::repository.isInitialized) return flowOf(emptyList())
        
        return when (source) {
            "user" -> repository.getWordsBySource("user")
                .map { it.shuffled().take(limit) }
            "quranic" -> repository.getWordsBySource("quranic")
                .map { it.shuffled().take(limit) }
            "both" -> {
                combine(
                    repository.getWordsBySource("user"),
                    repository.getWordsBySource("quranic")
                ) { userWords, quranicWords ->
                    (userWords + quranicWords).shuffled().take(limit)
                }
            }
            else -> flowOf(emptyList())
        }
    }
}