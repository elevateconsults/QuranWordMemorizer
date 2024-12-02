package store.jaranation.quranwordmemorizer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import store.jaranation.quranwordmemorizer.data.repository.WordRepository

class WordViewModelFactory(private val repository: WordRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
            val viewModel = WordViewModel()
            viewModel.repository = repository
            viewModel.initialize()
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}