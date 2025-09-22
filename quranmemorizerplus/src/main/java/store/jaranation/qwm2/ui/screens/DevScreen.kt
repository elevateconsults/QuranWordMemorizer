package store.jaranation.qwm2.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import store.jaranation.qwm2.data.Word
import store.jaranation.qwm2.data.WordSource
import store.jaranation.qwm2.ui.viewmodel.WordViewModel
import store.jaranation.qwm2.util.NotificationUtils
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevScreen(onBack: () -> Unit, onStartPractice: () -> Unit) {
    val vm: WordViewModel = viewModel()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text("Developer / Test") }) }) { inner ->
        Column(
            modifier = Modifier.fillMaxSize().padding(inner).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(onClick = {
                scope.launch {
                    vm.clear()
                    val samples = listOf(
                        Word(arabic = "رَحْمَة", transliteration = "rahmah", meaning = "mercy", source = WordSource.QURANIC),
                        Word(arabic = "نُور", transliteration = "noor", meaning = "light", source = WordSource.QURANIC),
                        Word(arabic = "حِكْمَة", transliteration = "hikmah", meaning = "wisdom", source = WordSource.QURANIC)
                    )
                    samples.forEach { vm.add(it) }
                }
            }) { Text("Seed sample words") }

            Button(onClick = { NotificationUtils.showSimpleNotification(
                context = context,
                title = "QWM+ Test",
                text = "This is a test notification"
            ) }) { Text("Show simple notification now") }

            Button(onClick = {
                // Enqueue background test notification using WorkManager
                NotificationUtils.enqueueTestNotification(context = context)
            }) { Text("Enqueue notification worker (1x)") }

            Button(onClick = onStartPractice) { Text("Start Practice (3 Q) – placeholder") }
            Button(onClick = onBack) { Text("Back") }
        }
    }
}
