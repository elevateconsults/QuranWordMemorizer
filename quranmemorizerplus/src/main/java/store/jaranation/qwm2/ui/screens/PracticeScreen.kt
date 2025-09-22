package store.jaranation.qwm2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import store.jaranation.qwm2.data.Word
import store.jaranation.qwm2.ui.viewmodel.WordViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.collectAsState

@Composable
fun PracticeScreen(onBack: () -> Unit, vm: WordViewModel = viewModel()) {
    val words = vm.words.collectAsState().value
    var currentIndex by mutableIntStateOf(0)
    var score by mutableIntStateOf(0)
    val totalQuestions = 3
    var options by mutableStateOf(listOf<String>())
    var finished by mutableStateOf(false)

    LaunchedEffect(words, currentIndex) {
        if (words.isNotEmpty() && currentIndex < minOf(totalQuestions, words.size)) {
            val correct = words[currentIndex]
            val distractors = words.filter { it.id != correct.id }.shuffled().take(2).map { it.meaning }
            options = (distractors + correct.meaning).shuffled()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        when {
            words.isEmpty() -> {
                Text("No words found. Go to Word Bank or Dev screen to add/seed words.")
                Button(onClick = onBack) { Text("Back") }
            }
            finished -> {
                Text("Done! Score: $score / ${minOf(totalQuestions, words.size)}")
                Button(onClick = onBack) { Text("Back") }
            }
            else -> {
                val w: Word = words[currentIndex]
                Text(text = "What is the meaning of:")
                Card(colors = CardDefaults.cardColors()) {
                    Column(Modifier.padding(16.dp)) {
                        Text(text = w.arabic)
                        Spacer(Modifier.height(4.dp))
                        Text(text = w.transliteration)
                    }
                }
                options.forEach { opt ->
                    OutlinedButton(onClick = {
                        if (opt == w.meaning) score++
                        val next = currentIndex + 1
                        if (next >= minOf(totalQuestions, words.size)) {
                            finished = true
                        } else {
                            currentIndex = next
                        }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text(opt)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = onBack) { Text("Exit") }
            }
        }
    }
}
