package store.jaranation.quranwordmemorizer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import store.jaranation.quranwordmemorizer.data.local.Word
import store.jaranation.quranwordmemorizer.ui.viewmodels.WordViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordEntryScreen(
    wordViewModel: WordViewModel,
    onNavigateBack: () -> Unit
) {
    var arabicWord by remember { mutableStateOf("") }
    var englishMeaning by remember { mutableStateOf("") }
    var transliteration by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = { Text("Add New Word") },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = arabicWord,
            onValueChange = { arabicWord = it },
            label = { Text("Arabic Word") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = englishMeaning,
            onValueChange = { englishMeaning = it },
            label = { Text("English Meaning") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = transliteration,
            onValueChange = { transliteration = it },
            label = { Text("Transliteration (Optional)") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (arabicWord.isNotBlank() && englishMeaning.isNotBlank()) {
                    val word = Word(
                        arabicWord = arabicWord,
                        englishMeaning = englishMeaning,
                        transliteration = transliteration.takeIf { it.isNotBlank() },
                        source = "user"
                    )
                    wordViewModel.addWord(word)
                    showSuccessMessage = true
                    arabicWord = ""
                    englishMeaning = ""
                    transliteration = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = arabicWord.isNotBlank() && englishMeaning.isNotBlank()
        ) {
            Text("Add Word")
        }

        if (showSuccessMessage) {
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay(2000)
                showSuccessMessage = false
            }
            Snackbar(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Word added successfully!")
            }
        }
    }
}
