package store.jaranation.quranwordmemorizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import store.jaranation.quranwordmemorizer.ui.theme.QuranWordMemorizerTheme
import store.jaranation.quranwordmemorizer.ui.screens.HomeScreen
import store.jaranation.quranwordmemorizer.ui.screens.WordBankScreen
import store.jaranation.quranwordmemorizer.ui.screens.QuizScreen
import store.jaranation.quranwordmemorizer.ui.screens.SettingsScreen
import store.jaranation.quranwordmemorizer.ui.screens.TestSettingsScreen
import store.jaranation.quranwordmemorizer.quiz.QuizScheduler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Schedule quiz notifications
        QuizScheduler.scheduleQuizNotifications(this)
        
        setContent {
            QuranWordMemorizerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    QuranWordMemorizerApp()
                }
            }
        }
    }
}

@Composable
fun QuranWordMemorizerApp() {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToWordBank = { navController.navigate("wordbank") },
                    onNavigateToQuiz = { navController.navigate("quiz") },
                    onNavigateToSettings = { navController.navigate("settings") },
                    onNavigateToTestSettings = { navController.navigate("test_settings") }
                )
            }
            composable("wordbank") {
                WordBankScreen(
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToSettings = { navController.navigate("settings") }
                )
            }
            composable("quiz") {
                QuizScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable("settings") {
                SettingsScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable("test_settings") {
                TestSettingsScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}