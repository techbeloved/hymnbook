package com.techbeloved.hymnbook.tunesplayback

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.StringDef
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.jakewharton.rxrelay2.PublishRelay
import com.techbeloved.hymnbook.data.PlayerPreferences
import com.techbeloved.hymnbook.di.Injection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

class TunesPlayerService : MediaBrowserServiceCompat() {

    private lateinit var becomingNoisyReceiver: BecomingNoisyReceiver
    private lateinit var mediaController: MediaControllerCompat
    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var stateBuilder: PlaybackStateCompat.Builder
    private lateinit var metadataBuilder: MediaMetadataCompat.Builder
    private lateinit var playback: MediaPlayback // Where to initialize the player

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationBuilder
    private lateinit var playbackStateBuilder: PlaybackStateCompat.Builder

    private lateinit var playbackPrefs: PlayerPreferences

    private var isForegroundService = false

    private var lastPlaybackState: Int = PlaybackStateCompat.STATE_NONE

    private val disposables = CompositeDisposable()

    /**
     * Receives stop requests and starts a delayed timer
     */
    private val stopSubject = PublishSubject.create<Int>()

    private val mediaControllerCallback: MediaControllerCallback by lazy {
        MediaControllerCallback()
    }


    override fun onCreate() {
        super.onCreate()

        playbackPrefs = Injection.providePlayerPrefs

        val sessionActivityPendingIntent =
                packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                    PendingIntent.getActivity(this, 0, sessionIntent, 0)
                }

        // Create a media session
        mediaSession = MediaSessionCompat(baseContext, TAG).apply {

            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS)

            stateBuilder = PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_PLAY
                            or PlaybackStateCompat.ACTION_PLAY_PAUSE)

            setPlaybackState(stateBuilder.build())

            setCallback(MediaSessionCallback())

            setSessionToken(sessionToken)

            setSessionActivity(sessionActivityPendingIntent)
        }

        mediaController = MediaControllerCompat(this, mediaSession).also {
            it.registerCallback(mediaControllerCallback)
        }

        becomingNoisyReceiver =
                BecomingNoisyReceiver(context = this, sessionToken = mediaSession.sessionToken)

        notificationBuilder = NotificationBuilder(this)
        notificationManager = NotificationManagerCompat.from(this)

        metadataBuilder = MediaMetadataCompat.Builder()
        playbackStateBuilder = PlaybackStateCompat.Builder()

        playback = MediaPlayerAdapter()

        val disposable = playback.playbackStatus().subscribe(
                { status ->
                    if (mediaSession.isActive) {
                        when (status) {

                            is PlaybackStatus.PlaybackComplete -> {
                                setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
                            }
                            is PlaybackStatus.PlaybackError -> setMediaPlaybackState(PlaybackStateCompat.STATE_ERROR)
                        }
                    }
                },
                {
                    Timber.w(it)

                }
        )

        disposables.add(disposable)

        playbackPrefs.playbackRate()
                .subscribe(
                        { rate ->
                            if (mediaSession.isActive) {

                                val args = Bundle().apply {
                                    putFloat(ARGS_PLAYBACK_RATE, rate)
                                }
                                mediaController.transportControls.sendCustomAction(ACTION_PLAYBACK_RATE, args)
                            }
                        }, { Timber.w(it) }
                )
                .let { disposables.add(it) }

        playbackPrefs.repeatMode()
                .distinctUntilChanged()
                .subscribe({ repeatMode ->
                    if (mediaSession.isActive) {
                        mediaController.transportControls.setRepeatMode(repeatMode)
                    }

                }, { Timber.w(it) })
                .let { disposables.add(it) }

        stopSubject.switchMap { request ->
            when (request) {
                REQUEST_DELAYED_STOP -> {
                    Timber.i("stop request sent")
                    Observable.timer(30, TimeUnit.SECONDS)
                }
                else -> {
                    Timber.i("Stop request cancelled")
                    Observable.empty()
                }
            }
        }.subscribe({
            Timber.i("About stopping service")
            notificationManager.cancelAll()
            stopForeground(true)
            stopSelf()
        }, { Timber.w(it) })
                .let { disposables.add(it) }

    }

    override fun onDestroy() {
        super.onDestroy()
        if (!disposables.isDisposed) disposables.dispose()
        mediaController.unregisterCallback(mediaControllerCallback)
        notificationManager.cancelAll()
        mediaSession.release()
    }


    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        if (EMPTY_MEDIA_ROOT_ID == parentId) {
            result.sendResult(null)
            return
        }

        val mediaItems: MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()

        result.sendResult(mediaItems)
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(EMPTY_MEDIA_ROOT_ID, null)
    }

    private fun removeNowPlayingNotification() {
        stopForeground(true)
    }

    private fun setMediaPlaybackState(state: Int) {
        val playbackPosition = playback.currentPosition()
        val playbackRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            playback.playbackRate()
        } else {
            1.0f
        }
        when (state) {
            PlaybackStateCompat.STATE_PLAYING -> {
                playbackStateBuilder.setActions(
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                                or PlaybackStateCompat.ACTION_PAUSE
                )
                playbackStateBuilder.setState(state, playbackPosition, playbackRate)
                stopSubject.onNext(CANCEL_STOP_REQUEST)
            }
            PlaybackStateCompat.STATE_PAUSED -> {
                playbackStateBuilder.setActions(
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                                or PlaybackStateCompat.ACTION_PLAY
                )
                playbackStateBuilder.setState(state, playbackPosition, playbackRate)
                stopSubject.onNext(REQUEST_DELAYED_STOP)
            }
            else -> {
                playbackStateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0.0f)
                stopSubject.onNext(REQUEST_DELAYED_STOP)
            }
        }
        mediaSession.setPlaybackState(playbackStateBuilder.build())
        lastPlaybackState = state
    }

    /**
     * Handles all the controls sent from media controller
     */
    inner class MediaSessionCallback : MediaSessionCompat.Callback() {

        private lateinit var audioFocusRequest: AudioFocusRequest

        private val audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager


        private var shouldPlayOnFocusGain: Boolean = false

        private var playbackRate: Float = 1.0f

        private var numVerses: Int = 1

        @PlaybackStateCompat.RepeatMode
        private var repeatMode: Int = PlaybackStateCompat.REPEAT_MODE_NONE

        private val playFromMediaIdSubject = PublishRelay.create<String>()

        init {
            // Initialize player settings from shared preferences
            playbackRate = playbackPrefs.playbackRate().blockingFirst()
            repeatMode = playbackPrefs.repeatMode().blockingFirst()

            playFromMediaIdSubject.switchMapSingle { mediaId ->
                Injection.provideRepository.getHymnById(mediaId.toInt())
                        .firstOrError()
            }
                    .subscribe({ hymn ->
                        // Check that the hymn is playable.
                        if (hymn.audio?.midi.isNullOrBlank()
                                && hymn.audio?.mp3.isNullOrBlank()) {
                            // Let the client know that there's an error.
                            mediaSession.sendSessionEvent(EVENT_PLAYABLE_MEDIA_NOT_AVAILABLE,
                                    Bundle().apply { putInt(EXTRA_HYMN_ID, hymn.num) })
                            return@subscribe
                        }

                        val mp3 = hymn.audio?.mp3
                        val midi = hymn?.audio?.midi
                        if (!File(midi).exists() && !File(mp3).exists()) {
                            mediaSession.sendSessionEvent(EVENT_MEDIA_FILE_NOT_FOUND,
                                    Bundle().apply { putInt(EXTRA_HYMN_ID, hymn.num) })
                            return@subscribe
                        }

                        numVerses = hymn.verses.size
                        // Create the metadata
                        metadataBuilder.id = hymn.num.toString()
                        metadataBuilder.title = hymn.title
                        metadataBuilder.displaySubtitle = hymn.first
                        metadataBuilder.artist = hymn.attribution?.musicBy
                        metadataBuilder.mediaUri = hymn.audio?.midi ?: hymn.audio?.mp3
                        metadataBuilder.album = "Watchman Hymnbook"

                        // Set repeat mode before preparing. onPrepare uses the value set in repeat mode
                        playback.setRepeat(repeatMode, numVerses)

                        val metadata = metadataBuilder.build()
                        val preparedDuration = try {
                            playback.prepare(metadata).blockingGet(0)
                        } catch (e: Exception) {
                            Timber.w(e, "MediaPlayer cannot play file")
                            0
                        }
                        if (preparedDuration > 0) {
                            // Set the session metadata
                            metadataBuilder.duration = preparedDuration.toLong()
                            mediaSession.setMetadata(metadataBuilder.build())
                            onPlay()
                        }

                    }, { Timber.w(it, "Error getting hymn") })
                    .let { disposables.add(it) }
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            playFromMediaIdSubject.accept(mediaId)
        }


        override fun onPlay() {

            if (getAudioFocus()) {
                mediaSession.isActive = true
                // Start service is handled in controller callback which is triggered when set state is called on session
                // Likewise noisy audio receiver and notification.
                // We just have to set the correct state here, start the player and move on
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    playback.setPlaybackSpeed(playbackRate)
                }
                // Start player
                playback.onPlay()
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING)
            }
        }

        override fun onPause() {
            // Handles notification and noisy receiver unregistering
            playback.onPause()
            setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED)
            discardAudioFocus()
        }

        override fun onStop() {
            discardAudioFocus()
            // Stop service
            setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED)
            stopSelf()
            mediaSession.isActive = false
            playback.onStop()
        }

        override fun onCustomAction(action: String?, extras: Bundle?) {
            if (action == ACTION_PLAYBACK_RATE) {
                val playbackRate = extras?.getFloat(ARGS_PLAYBACK_RATE) ?: 1.0f
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    playback.setPlaybackSpeed(playbackRate)
                }
                this.playbackRate = playbackRate // Save in property
            }
        }

        override fun onSetRepeatMode(repeatMode: Int) {
            this.repeatMode = repeatMode
            playback.setRepeat(repeatMode, numVerses)
            playbackPrefs.saveRepeatMode(repeatMode)
            // Refresh the play state so progress bar updates correctly
            setMediaPlaybackState(lastPlaybackState)
            Timber.i("Setting repeat mode")
        }


        private fun discardAudioFocus() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioManager.abandonAudioFocusRequest(audioFocusRequest)
            } else {
                audioManager.abandonAudioFocus(audioFocusChangeListener)
            }
        }

        private fun getAudioFocus(): Boolean {
            val result: Int
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).run {
                    setOnAudioFocusChangeListener(audioFocusChangeListener)
                    setAudioAttributes(AudioAttributes.Builder().run {
                        setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        build()
                    })
                    build()
                }

                result = audioManager.requestAudioFocus(audioFocusRequest)
            } else {
                // DONE: request audio focus for lower android versions
                result = audioManager.requestAudioFocus(
                        audioFocusChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN
                )
            }

            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }

        private val audioFocusChangeListener: AudioManager.OnAudioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
            when (focusChange) {
                AudioManager.AUDIOFOCUS_LOSS -> {
                    playback.onPause()
                    shouldPlayOnFocusGain = false
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    if (playback.isPlaying()) {
                        playback.onPause()
                        shouldPlayOnFocusGain = true // We want to resume later
                    } else {
                        shouldPlayOnFocusGain = false
                    }
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    if (playback.isPlaying()) {
                        playback.duck()
                        shouldPlayOnFocusGain = true
                    }
                }
                AudioManager.AUDIOFOCUS_GAIN -> {
                    if (shouldPlayOnFocusGain) {
                        playback.onPlay()
                    }
                }
            }
        }


    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            mediaController.playbackState?.let { updateNotification(it) }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            mediaController.playbackState?.let { updateNotification(it) }
        }

        private fun updateNotification(state: PlaybackStateCompat) {
            val updatedState = state.state

            // Skip building a notification when state is "none" and metadata is null
            val notification = if (mediaController.metadata != null
                    && updatedState != PlaybackStateCompat.STATE_NONE) {
                notificationBuilder.buildNotification(mediaSession.sessionToken)
            } else {
                null
            }

            when (updatedState) {
                PlaybackStateCompat.STATE_BUFFERING,
                PlaybackStateCompat.STATE_PLAYING -> {
                    becomingNoisyReceiver.register()

                    if (notification != null) {
                        notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)

                        if (!isForegroundService) {
                            ContextCompat.startForegroundService(
                                    applicationContext,
                                    Intent(applicationContext, this@TunesPlayerService.javaClass)
                            )
                            startForeground(NOW_PLAYING_NOTIFICATION, notification)
                            isForegroundService = true
                        }
                    }
                }
                else -> {
                    becomingNoisyReceiver.unregister()

                    if (isForegroundService) {
                        stopForeground(false)
                        isForegroundService = false

                        // If playback has ended, also stop the service.
                        if (updatedState == PlaybackStateCompat.STATE_NONE) {
                            stopSelf()
                        }

                        if (notification != null) {
                            notificationManager.notify(NOW_PLAYING_NOTIFICATION, notification)
                        } else {
                            removeNowPlayingNotification()
                        }
                    }
                }
            }
        }
    }

    companion object {
        const val EMPTY_MEDIA_ROOT_ID = "EMPTY_MEDIA_ROOT_ID"
        const val TAG = "TunesPlayerService"
    }
}

private class BecomingNoisyReceiver(
        private val context: Context,
        sessionToken: MediaSessionCompat.Token
) : BroadcastReceiver() {

    private val noisyIntentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
    private val controller = MediaControllerCompat(context, sessionToken)

    private var registered = false

    fun register() {
        if (!registered) {
            context.registerReceiver(this, noisyIntentFilter)
            registered = true
        }
    }

    fun unregister() {
        if (registered) {
            context.unregisterReceiver(this)
            registered = false
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == AudioManager.ACTION_AUDIO_BECOMING_NOISY) {
            controller.transportControls.pause()
        }
    }
}

@StringDef(ACTION_PLAYBACK_RATE)
@Retention(AnnotationRetention.SOURCE)
annotation class MediaAction

const val ACTION_PLAYBACK_RATE = "playbackTempo"

const val ARGS_PLAYBACK_RATE = "argsPlaybackRate"

const val EVENT_INVALID_ITEM = "eventInvalidItem"
const val EVENT_PLAYABLE_MEDIA_NOT_AVAILABLE = "mediaNotAvailable"
const val EVENT_MEDIA_FILE_NOT_FOUND = "mediaFileNotFound"

const val EXTRA_HYMN_ID = "extraHymnId"

private const val REQUEST_DELAYED_STOP = 411
private const val CANCEL_STOP_REQUEST = 200