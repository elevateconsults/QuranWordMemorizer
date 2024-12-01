package store.jaranation.quranwordmemorizer.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import store.jaranation.quranwordmemorizer.data.preferences.QuizSettings
import store.jaranation.quranwordmemorizer.ui.viewmodels.SettingsViewModel
import store.jaranation.quranwordmemorizer.ui.components.TimePickerDialog
import store.jaranation.quranwordmemorizer.ui.components.TimeSelector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    val settings by viewModel.settings.collectAsState(initial = QuizSettings())
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz Settings") },
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
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Notifications Switch
            Card {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Quiz Notifications")
                    Switch(
                        checked = settings.isNotificationEnabled,
                        onCheckedChange = { viewModel.updateNotificationEnabled(it) }
                    )
                }
            }

            // Quiz Source Selection
            if (settings.isNotificationEnabled) {
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Quiz Source",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            QuizSourceOption(
                                title = "Quranic Words",
                                description = "Quiz from all Quranic words",
                                selected = settings.quizSource == "quranic",
                                onClick = { viewModel.updateQuizSource("quranic") }
                            )

                            QuizSourceOption(
                                title = "My Words",
                                description = "Quiz from your added words",
                                selected = settings.quizSource == "user",
                                onClick = { viewModel.updateQuizSource("user") }
                            )

                            QuizSourceOption(
                                title = "Both Sources",
                                description = "Quiz from both Quranic and your words",
                                selected = settings.quizSource == "both",
                                onClick = { viewModel.updateQuizSource("both") }
                            )
                        }
                    }
                }

                // Quiz Frequency Selection
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Quiz Frequency",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // Preset frequencies in a LazyRow for horizontal scrolling
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(listOf(1, 2, 3, 5, 10, 15, 30, 45, 60, 120)) { minutes ->
                                FrequencyChip(
                                    minutes = minutes,
                                    selected = settings.notificationFrequency == minutes,
                                    onClick = { viewModel.updateNotificationFrequency(minutes) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Custom frequency input
                        var showCustomFrequency by remember { mutableStateOf(false) }
                        var customMinutes by remember { mutableStateOf("") }
                        
                        OutlinedButton(
                            onClick = { showCustomFrequency = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Custom Frequency")
                        }

                        if (showCustomFrequency) {
                            AlertDialog(
                                onDismissRequest = { showCustomFrequency = false },
                                title = { Text("Set Custom Frequency") },
                                text = {
                                    Column {
                                        OutlinedTextField(
                                            value = customMinutes,
                                            onValueChange = { customMinutes = it },
                                            label = { Text("Minutes") },
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            singleLine = true,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                        if (customMinutes.isNotEmpty()) {
                                            Text(
                                                text = formatFrequency(customMinutes.toIntOrNull() ?: 0),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                modifier = Modifier.padding(top = 8.dp)
                                            )
                                        }
                                    }
                                },
                                confirmButton = {
                                    TextButton(
                                        onClick = {
                                            customMinutes.toIntOrNull()?.let { minutes ->
                                                if (minutes > 0) {
                                                    viewModel.updateNotificationFrequency(minutes)
                                                    showCustomFrequency = false
                                                }
                                            }
                                        }
                                    ) {
                                        Text("Set")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showCustomFrequency = false }) {
                                        Text("Cancel")
                                    }
                                }
                            )
                        }

                        // Show current frequency
                        if (settings.notificationFrequency > 0) {
                            Text(
                                text = "Current frequency: ${formatFrequency(settings.notificationFrequency)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Active Hours Selection
                Card {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Active Hours",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Only send notifications during these hours",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TimeButton(
                                label = "Start Time",
                                time = settings.startTime,
                                onClick = { showStartTimePicker = true }
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = "to"
                            )
                            TimeButton(
                                label = "End Time",
                                time = settings.endTime,
                                onClick = { showEndTimePicker = true }
                            )
                        }

                        // Common time presets
                        Column(
                            modifier = Modifier.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            TimePreset(
                                label = "Morning (6 AM - 10 AM)",
                                startTime = 6,
                                endTime = 10,
                                isSelected = settings.startTime == 6 && settings.endTime == 10,
                                onClick = {
                                    viewModel.updateTimeRange(6, 10)
                                }
                            )
                            TimePreset(
                                label = "Afternoon (12 PM - 4 PM)",
                                startTime = 12,
                                endTime = 16,
                                isSelected = settings.startTime == 12 && settings.endTime == 16,
                                onClick = {
                                    viewModel.updateTimeRange(12, 16)
                                }
                            )
                            TimePreset(
                                label = "Evening (5 PM - 9 PM)",
                                startTime = 17,
                                endTime = 21,
                                isSelected = settings.startTime == 17 && settings.endTime == 21,
                                onClick = {
                                    viewModel.updateTimeRange(17, 21)
                                }
                            )
                        }
                    }
                }
            }

            // Test Notification Section (at the bottom)
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Test Notification",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.sendTestNotification() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send Test Quiz Notification")
                    }
                    Text(
                        text = "Tap to see how quiz notifications will appear",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Testing Options
            Card {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Testing Options",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Test Mode (One-Click Test)")
                            Text(
                                "Enable instant notifications for testing",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = settings.isTestMode,
                            onCheckedChange = { viewModel.updateTestMode(it) }
                        )
                    }

                    if (settings.isTestMode) {
                        Button(
                            onClick = { viewModel.sendTestNotification() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp)
                        ) {
                            Text("Send Test Notification Now")
                        }
                    }
                }
            }
        }
    }

    // Time Picker Dialogs
    if (showStartTimePicker) {
        TimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onTimeSelected = { hour ->
                if (hour <= settings.endTime) {
                    viewModel.updateTimeRange(hour, settings.endTime)
                } else {
                    // If start time is after end time, adjust end time
                    viewModel.updateTimeRange(hour, hour)
                }
                showStartTimePicker = false
            },
            initialHour = settings.startTime
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismiss = { showEndTimePicker = false },
            onTimeSelected = { hour ->
                if (hour >= settings.startTime) {
                    viewModel.updateTimeRange(settings.startTime, hour)
                } else {
                    // If end time is before start time, adjust start time
                    viewModel.updateTimeRange(hour, hour)
                }
                showEndTimePicker = false
            },
            initialHour = settings.endTime
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuizSourceOption(
    title: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatFrequency(minutes: Int): String {
    return when {
        minutes < 60 -> "$minutes minutes"
        minutes == 60 -> "1 hour"
        minutes % 60 == 0 -> "${minutes / 60} hours"
        else -> "${minutes / 60}h ${minutes % 60}m"
    }
}

@Composable
private fun TimeButton(
    label: String,
    time: Int,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        FilledTonalButton(
            onClick = onClick,
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = String.format("%02d:00", time),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePreset(
    label: String,
    startTime: Int,
    endTime: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text("$label (${formatTime(startTime)}-${formatTime(endTime)})")
        }
    )
}

private fun formatTime(hour: Int): String {
    val period = if (hour < 12) "AM" else "PM"
    val displayHour = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return "$displayHour $period"
}

@Composable
private fun FrequencyChip(
    minutes: Int,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (selected)
            MaterialTheme.colorScheme.primaryContainer
        else
            MaterialTheme.colorScheme.surface,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Text(
            text = "${minutes}m",
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            color = if (selected)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurface
        )
    }
}