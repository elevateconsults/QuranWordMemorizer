package store.jaranation.qwm2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.clickable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import store.jaranation.qwm2.ui.theme.QWMPlusTheme
import store.jaranation.qwm2.ui.screens.DevScreen
import store.jaranation.qwm2.ui.screens.PracticeScreen
import store.jaranation.qwm2.ui.screens.ProgressScreen
import store.jaranation.qwm2.ui.screens.SettingsScreen
import store.jaranation.qwm2.ui.screens.WordBankScreen
import store.jaranation.qwm2.util.NotificationUtils
import android.os.Build

class MainActivity : ComponentActivity() {
    private val requestNotifPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { _ -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationUtils.createNotificationChannel(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotifPermission.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            QWMPlusTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    QuranMemorizerPlusApp()
                }
            }
        }
    }
}

@Composable
fun QuranMemorizerPlusApp() {
    val navController = rememberNavController()
    Scaffold { inner ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(inner)
        ) {
            composable("home") {
                HomeScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable("practice") { PracticeScreen(onBack = { navController.navigateUp() }) }
            composable("wordbank") { WordBankScreen(onBack = { navController.navigateUp() }) }
            composable("progress") { ProgressScreen(onBack = { navController.navigateUp() }) }
            composable("settings") { SettingsScreen(onBack = { navController.navigateUp() }) }
            composable("dev") {
                DevScreen(
                    onBack = { navController.navigateUp() },
                    onStartPractice = { navController.navigate("practice") }
                )
            }
        }
    }
}

@Composable
fun HomeScreen(onNavigate: (String) -> Unit, contentPadding: PaddingValues = PaddingValues(16.dp)) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Quran Word Memorizer Plus",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Card(modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate("practice") }, colors = CardDefaults.cardColors()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Practice Now", style = MaterialTheme.typography.titleMedium)
                Text("Start a quick quiz", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Card(modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate("wordbank") }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Word Bank", style = MaterialTheme.typography.titleMedium)
                Text("Browse, add, and manage words", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Card(modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate("progress") }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Progress", style = MaterialTheme.typography.titleMedium)
                Text("View your stats and streak", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Card(modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate("settings") }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Settings", style = MaterialTheme.typography.titleMedium)
                Text("Notifications, quiz length, and more", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Card(modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigate("dev") }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Developer / Test", style = MaterialTheme.typography.titleMedium)
                Text("Seed words, trigger notification, quick tests", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
