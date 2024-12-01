package store.jaranation.quranwordmemorizer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import store.jaranation.quranwordmemorizer.data.local.Word
import store.jaranation.quranwordmemorizer.ui.viewmodels.WordSelectionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordSelectionScreen(
    onNavigateBack: () -> Unit,
    viewModel: WordSelectionViewModel = viewModel()
) {
    val words by viewModel.userWords.collectAsState(initial = emptyList())
    val selectedWords by viewModel.selectedWords.collectAsState()
    val quizDuration by viewModel.quizDuration.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Words for Quiz") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveSelection()
                            onNavigateBack()
                        }
                    ) {
                        Text("Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Duration Selection
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quiz Duration",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(1, 3, 7, 14, 30).forEach { days ->
                            FilterChip(
                                selected = quizDuration == days,
                                onClick = { viewModel.updateQuizDuration(days) },
                                label = { Text("${days}d") }
                            )
                        }
                    }
                }
            }

            // Word Selection List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                items(words) { word ->
                    WordSelectionItem(
                        word = word,
                        isSelected = selectedWords.contains(word.id),
                        onSelectionChanged = { selected ->
                            if (selected) {
                                viewModel.selectWord(word.id)
                            } else {
                                viewModel.deselectWord(word.id)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun WordSelectionItem(
    word: Word,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onSelectionChanged(!isSelected) }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = word.arabicWord,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = word.englishMeaning,
                    style = MaterialTheme.typography.bodyMedium
                )
                word.transliteration?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = onSelectionChanged
            )
        }
    }
} 