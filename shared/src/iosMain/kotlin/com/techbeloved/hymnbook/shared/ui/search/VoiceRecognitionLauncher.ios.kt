package com.techbeloved.hymnbook.shared.ui.search

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioEngine
import platform.AVFAudio.AVAudioPCMBuffer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionDuckOthers
import platform.AVFAudio.AVAudioSessionCategoryRecord
import platform.AVFAudio.AVAudioSessionModeMeasurement
import platform.AVFAudio.AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
import platform.AVFAudio.AVAudioTime
import platform.AVFAudio.setActive
import platform.Foundation.NSError
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Speech.SFSpeechAudioBufferRecognitionRequest
import platform.Speech.SFSpeechRecognitionResult
import platform.Speech.SFSpeechRecognitionTask
import platform.Speech.SFSpeechRecognizer
import platform.Speech.SFSpeechRecognizerAuthorizationStatus
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
public actual fun rememberVoiceRecognitionLauncher(onResult: (String?) -> Unit): VoiceRecognitionLauncher {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        VoiceSearchDialog(
            onDismissRequest = { showDialog = false },
            onResult = { result ->
                showDialog = false
                onResult(result)
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

@OptIn(ExperimentalForeignApi::class)
@Composable
private fun VoiceSearchDialog(
    onDismissRequest: () -> Unit,
    onResult: (String?) -> Unit,
) {
    var spokenText by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val speechRecognizer = remember { SFSpeechRecognizer(locale = NSLocale.currentLocale) }
    val audioEngine = remember { AVAudioEngine() }
    var recognitionRequest: SFSpeechAudioBufferRecognitionRequest? by remember { mutableStateOf(null) }
    var recognitionTask: SFSpeechRecognitionTask? by remember { mutableStateOf(null) }

    DisposableEffect(Unit) {
        fun stopRecording() {
            if (audioEngine.isRunning()) {
                audioEngine.stop()
                audioEngine.inputNode.removeTapOnBus(0u)
            }
            recognitionRequest?.endAudio()
            recognitionTask?.cancel()
            isListening = false
        }

        SFSpeechRecognizer.requestAuthorization { status ->
            dispatch_async(dispatch_get_main_queue()) {
                if (status == SFSpeechRecognizerAuthorizationStatus.SFSpeechRecognizerAuthorizationStatusAuthorized) {
                    if (audioEngine.isRunning()) {
                        stopRecording()
                        return@dispatch_async
                    }

                    val audioSession = AVAudioSession.sharedInstance()
                    try {
                        audioSession.setCategory(
                            AVAudioSessionCategoryRecord,
                            mode = AVAudioSessionModeMeasurement,
                            options = AVAudioSessionCategoryOptionDuckOthers,
                            error = null
                        )
                        audioSession.setActive(
                            true,
                            withOptions = AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation,
                            error = null
                        )
                    } catch (e: Exception) {
                        errorMessage = "Audio Session Error: ${e.message}"
                        return@dispatch_async
                    }

                    val node = audioEngine.inputNode
                    val recordingFormat = node.outputFormatForBus(0u)
                    node.installTapOnBus(
                        0u,
                        1024u,
                        recordingFormat
                    ) { buffer: AVAudioPCMBuffer?, time: AVAudioTime? ->
                        if (buffer != null) {
                            recognitionRequest?.appendAudioPCMBuffer(buffer)
                        }
                    }

                    audioEngine.prepare()

                    try {
                        audioEngine.startAndReturnError(null)
                    } catch (e: Exception) {
                        errorMessage = "Audio Engine Error"
                        return@dispatch_async
                    }

                    recognitionRequest = SFSpeechAudioBufferRecognitionRequest()
                    recognitionRequest?.shouldReportPartialResults = true

                    recognitionTask =
                        speechRecognizer?.recognitionTaskWithRequest(recognitionRequest!!) { result: SFSpeechRecognitionResult?, error: NSError? ->
                            dispatch_async(dispatch_get_main_queue()) {
                                var isFinal = false
                                if (result != null) {
                                    spokenText = result.bestTranscription.formattedString
                                    isFinal = result.isFinal()
                                }

                                if (error != null || isFinal) {
                                    stopRecording()
                                    if (error == null) {
                                        // Automatically finish? Or wait for user to click Done?
                                        // Let's keep dialog open so user can confirm
                                    } else {
                                        errorMessage = error.localizedDescription
                                    }
                                }
                            }
                        }
                } else {
                    errorMessage = "Speech recognition permission denied"
                    isListening = false
                }
            }
        }

        onDispose {
            stopRecording()
        }
    }


    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(if (isListening) "Listening..." else "Finished") },
        text = {
            if (errorMessage != null) {
                Text("Error: $errorMessage")
            } else {
                Text(spokenText.ifEmpty { "Say something..." })
            }
        },
        confirmButton = {
            Button(onClick = {
                onResult(spokenText)
            }) {
                Text("Done")
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}
