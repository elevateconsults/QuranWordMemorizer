package store.jaranation.qwm2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import store.jaranation.qwm2.data.Word
import store.jaranation.qwm2.ui.viewmodel.WordViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Row

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordBankScreen(onBack: () -> Unit, vm: WordViewModel = viewModel()) {
    var arabic by remember { mutableStateOf("") }
    var transliteration by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }

    val words = vm.words.collectAsState().value

    Scaffold(
        topBar = { TopAppBar(title = { Text("Word Bank") }) }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = arabic,
                onValueChange = { arabic = it },
                label = { Text("Arabic") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = transliteration,
                onValueChange = { transliteration = it },
                label = { Text("Transliteration") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = meaning,
                onValueChange = { meaning = it },
                label = { Text("Meaning") },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    if (arabic.isNotBlank() && meaning.isNotBlank()) {
                        vm.add(
                            Word(
                                arabic = arabic.trim(),
                                transliteration = transliteration.trim(),
                                meaning = meaning.trim()
                            )
                        )
                        arabic = ""; transliteration = ""; meaning = ""
                    }
                }) { Text("Add Word") }
                Button(onClick = onBack) { Text("Back") }
            }

            Spacer(Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(words) { w ->
                    Text("• ${w.arabic} — ${w.transliteration} — ${w.meaning}")
                }
            }
        }
    }
}
