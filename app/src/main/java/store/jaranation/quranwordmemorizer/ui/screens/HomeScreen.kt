package store.jaranation.quranwordmemorizer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onNavigateToWordBank: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTestSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onNavigateToWordBank,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Word Bank")
        }
        
        Button(
            onClick = onNavigateToQuiz,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Quiz")
        }
        
        Button(
            onClick = onNavigateToSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Settings")
        }

        Button(
            onClick = onNavigateToTestSettings,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Test Notifications")
        }
    }
}