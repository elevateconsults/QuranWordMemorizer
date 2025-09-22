package store.jaranation.qwm2.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import store.jaranation.qwm2.data.Word
import store.jaranation.qwm2.data.WordDatabase
import store.jaranation.qwm2.data.WordRepository

class WordViewModel(app: Application) : AndroidViewModel(app) {
    private val repo by lazy {
        val dao = WordDatabase.get(app).wordDao()
        WordRepository(dao)
    }

    val words = repo.observeAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun add(word: Word) = viewModelScope.launch { repo.add(word) }
    fun delete(word: Word) = viewModelScope.launch { repo.delete(word) }
    fun clear() = viewModelScope.launch { repo.clear() }
}
