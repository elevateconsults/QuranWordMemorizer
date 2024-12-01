package store.jaranation.quranwordmemorizer.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import store.jaranation.quranwordmemorizer.data.local.Word
import store.jaranation.quranwordmemorizer.data.local.WordDatabase
import store.jaranation.quranwordmemorizer.data.repository.WordRepository

@OptIn(ExperimentalCoroutinesApi::class)
class WordBankViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: WordRepository = WordRepository(
        WordDatabase.getDatabase(application).wordDao()
    )
    
    private val _currentSource = MutableStateFlow<String?>(null)
    val currentSource: StateFlow<String?> = _currentSource.asStateFlow()

    private val _quranicWordsCount = MutableStateFlow(0)
    val quranicWordsCount: StateFlow<Int> = _quranicWordsCount.asStateFlow()
    
    private val _userWordsCount = MutableStateFlow(0)
    val userWordsCount: StateFlow<Int> = _userWordsCount.asStateFlow()
    
    private val _totalWordsCount = MutableStateFlow(0)
    val totalWordsCount: StateFlow<Int> = _totalWordsCount.asStateFlow()

    init {
        refreshWordCounts()
    }

    val words: StateFlow<List<Word>> = _currentSource
        .flatMapLatest { source ->
            when (source) {
                null -> repository.allWords
                else -> repository.getWordsBySource(source)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setSource(source: String?) {
        _currentSource.value = source
    }

    fun insertWord(word: Word) = viewModelScope.launch {
        repository.insert(word)
    }

    fun updateWord(word: Word) = viewModelScope.launch {
        repository.update(word)
    }

    fun deleteWord(word: Word) = viewModelScope.launch {
        if (word.source == "user") {
            repository.delete(word)
        }
    }

    fun refreshWordCounts() {
        viewModelScope.launch {
            _quranicWordsCount.value = repository.getQuranicWordsCount()
            _userWordsCount.value = repository.getUserWordsCount()
            _totalWordsCount.value = repository.getTotalWordsCount()
        }
    }
} 