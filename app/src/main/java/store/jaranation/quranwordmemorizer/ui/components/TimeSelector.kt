package store.jaranation.quranwordmemorizer.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSelector(
    label: String,
    time: Int,
    onTimeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        ElevatedButton(
            onClick = { showDialog = true },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text(
                text = String.format("%02d:00", time),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }

    if (showDialog) {
        TimePickerDialog(
            onDismiss = { showDialog = false },
            onTimeSelected = { 
                onTimeSelected(it)
                showDialog = false
            },
            initialHour = time
        )
    }
} 