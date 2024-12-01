package store.jaranation.quranwordmemorizer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import store.jaranation.quranwordmemorizer.data.repository.WordRepository

class WordViewModel(private val repository: WordRepository) : ViewModel() {
    private val _quranicWordsCount = MutableStateFlow(0)
    val quranicWordsCount: StateFlow<Int> = _quranicWordsCount.asStateFlow()
    
    private val _userWordsCount = MutableStateFlow(0)
    val userWordsCount: StateFlow<Int> = _userWordsCount.asStateFlow()
    
    private val _totalWordsCount = MutableStateFlow(0)
    val totalWordsCount: StateFlow<Int> = _totalWordsCount.asStateFlow()
    
    init {
        viewModelScope.launch {
            _quranicWordsCount.value = repository.getQuranicWordsCount()
            _userWordsCount.value = repository.getUserWordsCount()
            _totalWordsCount.value = repository.getTotalWordsCount()
        }
    }
} 