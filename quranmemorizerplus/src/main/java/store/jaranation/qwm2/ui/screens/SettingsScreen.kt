package store.jaranation.qwm2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    val notificationsEnabled = remember { mutableStateOf(true) }
    val quizLength = remember { mutableIntStateOf(5) }

    Scaffold(topBar = { TopAppBar(title = { Text("Settings") }) }) { inner ->
        Column(
            modifier = Modifier.fillMaxSize().padding(inner).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Notifications")
            Switch(checked = notificationsEnabled.value, onCheckedChange = { notificationsEnabled.value = it })

            Text("Quiz length: ${quizLength.intValue}")
            Slider(
                value = quizLength.intValue.toFloat(),
                onValueChange = { quizLength.intValue = it.toInt() },
                valueRange = 3f..20f
            )

            Button(onClick = onBack) { Text("Back") }
        }
    }
}
