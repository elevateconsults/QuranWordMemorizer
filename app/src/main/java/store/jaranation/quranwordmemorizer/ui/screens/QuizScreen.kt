package store.jaranation.quranwordmemorizer.ui.screens

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import store.jaranation.quranwordmemorizer.quiz.QuestionType
import store.jaranation.quranwordmemorizer.ui.viewmodels.QuizViewModel
import store.jaranation.quranwordmemorizer.ui.viewmodels.QuizViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onNavigateBack: () -> Unit,
    viewModel: QuizViewModel = viewModel(
        factory = QuizViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val quizState by viewModel.quizState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Score display
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text("Correct: ${quizState.correctAnswers}")
                    Text("Total: ${quizState.totalAttempts}")
                }
            }

            if (quizState.isLoading) {
                CircularProgressIndicator()
            } else if (quizState.currentQuestion == null) {
                Text("No more questions available for now.")
            } else {
                quizState.currentQuestion?.let { q ->
                    // Question display
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = quizState.questionText,
                                style = MaterialTheme.typography.headlineLarge,
                                textAlign = TextAlign.Center
                            )
                            if (quizState.isAnswered) {
                                Text(
                                    text = q.correctAnswer,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (quizState.isCorrect)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    // Answer buttons
                    if (!quizState.isAnswered) {
                        q.options.forEach { option ->
                            Button(
                                onClick = { viewModel.checkAnswer(option) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(option)
                            }
                        }
                    } else {
                        Button(
                            onClick = { viewModel.loadNextQuestion() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Next Question")
                        }
                    }
                }
            }
        }
    }
} 