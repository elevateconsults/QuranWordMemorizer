package store.jaranation.quranwordmemorizer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import store.jaranation.quranwordmemorizer.ui.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestSettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Test Notifications") }
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
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Test Quiz Notifications",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Text(
                        text = "Click the button below to send a test quiz notification. " +
                              "You can interact with it to see how the quiz works.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Button(
                        onClick = { viewModel.sendTestNotification() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Test Quiz")
                    }
                }
            }

            // Instructions Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "How it works:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Text("1. Click 'Send Test Quiz' to receive a notification")
                    Text("2. Tap the notification to see the question")
                    Text("3. Choose one of the multiple choice answers")
                    Text("4. You'll receive feedback if your answer was correct")
                }
            }
        }
    }
}
