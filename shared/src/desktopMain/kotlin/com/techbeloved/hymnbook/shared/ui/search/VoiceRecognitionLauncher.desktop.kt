package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
public actual fun rememberVoiceRecognitionLauncher(onResult: (String?) -> Unit): VoiceRecognitionLauncher {
     var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Voice Search") },
            text = { Text("Voice search is not supported on this platform.") },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    return remember {
        object : VoiceRecognitionLauncher {
            override fun launch() {
                showDialog = true
            }
        }
    }
}
