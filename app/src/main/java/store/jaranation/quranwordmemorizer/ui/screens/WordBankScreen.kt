package store.jaranation.quranwordmemorizer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import store.jaranation.quranwordmemorizer.data.local.Word
import store.jaranation.quranwordmemorizer.ui.viewmodels.WordBankViewModel
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBankScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: WordBankViewModel = viewModel()
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf<Word?>(null) }
    val words by viewModel.words.collectAsState()
    val currentSource by viewModel.currentSource.collectAsState()
    val quranicWordsCount by viewModel.quranicWordsCount.collectAsState()
    val userWordsCount by viewModel.userWordsCount.collectAsState()
    val totalWordsCount by viewModel.totalWordsCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Word Bank") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Word")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Statistics Dashboard
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatisticItem(
                            label = "Quranic",
                            count = quranicWordsCount,
                            icon = Icons.Default.MenuBook,
                            color = MaterialTheme.colorScheme.primary
                        )
                        StatisticItem(
                            label = "My Words",
                            count = userWordsCount,
                            icon = Icons.Default.Create,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        StatisticItem(
                            label = "Total",
                            count = totalWordsCount,
                            icon = Icons.Default.List,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                    }
                }
            }

            // Filter Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = currentSource == null,
                    onClick = { viewModel.setSource(null) },
                    label = { Text("All Words") }
                )
                FilterChip(
                    selected = currentSource == "quranic",
                    onClick = { viewModel.setSource("quranic") },
                    label = { Text("Quranic Words") }
                )
                FilterChip(
                    selected = currentSource == "user",
                    onClick = { viewModel.setSource("user") },
                    label = { Text("My Words") }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Words List
            if (words.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No words yet. Click + to add a word.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    items(words) { word ->
                        WordItem(
                            word = word,
                            onEditClick = { if (word.source == "user") showEditDialog = word },
                            onDeleteClick = { if (word.source == "user") viewModel.deleteWord(word) }
                        )
                    }
                }
            }
        }
    }

    // Show dialogs
    if (showAddDialog) {
        AddWordDialog(
            onDismiss = { showAddDialog = false },
            onWordAdded = { word ->
                viewModel.insertWord(word)
                showAddDialog = false
            }
        )
    }

    showEditDialog?.let { word ->
        EditWordDialog(
            word = word,
            onDismiss = { showEditDialog = null },
            onWordUpdated = { updatedWord ->
                viewModel.updateWord(updatedWord)
                showEditDialog = null
            }
        )
    }
}

@Composable
fun WordItem(
    word: Word,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = word.arabicWord,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = word.englishMeaning,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    word.transliteration?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Text(
                        text = "Source: ${word.source}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (word.source == "user") {
                    Row {
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit")
                        }
                        IconButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddWordDialog(
    onDismiss: () -> Unit,
    onWordAdded: (Word) -> Unit
) {
    var arabicWord by remember { mutableStateOf("") }
    var englishMeaning by remember { mutableStateOf("") }
    var transliteration by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Word") },
        text = {
            Column {
                TextField(
                    value = arabicWord,
                    onValueChange = { arabicWord = it },
                    label = { Text("Arabic Word") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = englishMeaning,
                    onValueChange = { englishMeaning = it },
                    label = { Text("English Meaning") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = transliteration,
                    onValueChange = { transliteration = it },
                    label = { Text("Transliteration (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (arabicWord.isNotBlank() && englishMeaning.isNotBlank()) {
                        onWordAdded(
                            Word(
                                arabicWord = arabicWord,
                                englishMeaning = englishMeaning,
                                transliteration = transliteration.takeIf { it.isNotBlank() }
                            )
                        )
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditWordDialog(
    word: Word,
    onDismiss: () -> Unit,
    onWordUpdated: (Word) -> Unit
) {
    var arabicWord by remember { mutableStateOf(word.arabicWord) }
    var englishMeaning by remember { mutableStateOf(word.englishMeaning) }
    var transliteration by remember { mutableStateOf(word.transliteration ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Word") },
        text = {
            Column {
                TextField(
                    value = arabicWord,
                    onValueChange = { arabicWord = it },
                    label = { Text("Arabic Word") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = englishMeaning,
                    onValueChange = { englishMeaning = it },
                    label = { Text("English Meaning") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = transliteration,
                    onValueChange = { transliteration = it },
                    label = { Text("Transliteration (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (arabicWord.isNotBlank() && englishMeaning.isNotBlank()) {
                        onWordUpdated(
                            word.copy(
                                arabicWord = arabicWord,
                                englishMeaning = englishMeaning,
                                transliteration = transliteration.takeIf { it.isNotBlank() }
                            )
                        )
                    }
                }
            ) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun StatisticItem(
    label: String,
    count: Int,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
} 