package com.techbeloved.media

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryOptionDefaultToSpeaker
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionModeDefault
import platform.AVFAudio.setActive
import platform.Foundation.NSError

/**
 * Initializes and configures the audio session for playback.
 * This object is responsible for setting up the `AVAudioSession` on iOS and macOS
 * to ensure that audio playback behaves as expected (e.g., plays through speakers by default).
 *
 * The initialization is performed lazily when the `initialize` property is first accessed.
 */
@OptIn(ExperimentalForeignApi::class)
internal object AudioSessionInitializer {

    val initialize by lazy {
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val audioSession = AVAudioSession.sharedInstance()
            audioSession.run {
                setCategory(
                    category = AVAudioSessionCategoryPlayback,
                    mode = AVAudioSessionModeDefault,
                    options = AVAudioSessionCategoryOptionDefaultToSpeaker,
                    error = error.ptr,
                )
                setActive(active = true, error = error.ptr)
            }
        }
    }
}
