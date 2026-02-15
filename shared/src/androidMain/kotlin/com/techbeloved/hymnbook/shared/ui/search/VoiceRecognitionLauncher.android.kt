package com.techbeloved.hymnbook.shared.ui.search

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
public actual fun rememberVoiceRecognitionLauncher(onResult: (String?) -> Unit): VoiceRecognitionLauncher {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val results = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            onResult(results?.firstOrNull())
        }
    }

    return remember(launcher) {
        object : VoiceRecognitionLauncher {
            override fun launch() {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(
                        RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                    )
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now")
                }
                if (intent.resolveActivity(context.packageManager) != null) {
                    launcher.launch(intent)
                }
            }
        }
    }
}
