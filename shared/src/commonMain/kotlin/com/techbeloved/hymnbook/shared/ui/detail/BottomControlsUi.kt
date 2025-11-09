@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.techbeloved.hymnbook.shared.generated.Res
import com.techbeloved.hymnbook.shared.generated.content_description_media_not_available
import com.techbeloved.hymnbook.shared.generated.content_description_next
import com.techbeloved.hymnbook.shared.generated.content_description_pause
import com.techbeloved.hymnbook.shared.generated.content_description_play
import com.techbeloved.hymnbook.shared.generated.content_description_previous
import com.techbeloved.hymnbook.shared.ui.soundfonts.SoundFontDownloadButton
import com.techbeloved.media.AudioItem
import com.techbeloved.media.PlaybackController
import com.techbeloved.media.PlaybackState
import com.techbeloved.media.PlayerState
import com.techbeloved.media.rememberPlaybackController
import com.techbeloved.media.rememberPlaybackState
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun BottomControlsUi(
    audioItem: AudioItem?,
    isSoundFontDownloadRequired: Boolean,
    onPreviousButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit,
    onShowSoundFontSettings: () -> Unit,
    modifier: Modifier = Modifier,
    playbackState: PlaybackState = rememberPlaybackState(),
    controller: PlaybackController? = rememberPlaybackController(playbackState),
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier.animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(32.dp, alignment = Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FilledTonalIconButton(
                onClick = onPreviousButtonClick,
                shape = IconButtonDefaults.smallSquareShape,
                modifier = Modifier.size(56.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronLeft,
                    contentDescription = stringResource(Res.string.content_description_previous),
                )
            }
            when {
                isSoundFontDownloadRequired -> SoundFontDownloadButton(
                    onOpenSettingsClick = onShowSoundFontSettings,
                    modifier = Modifier.size(80.dp),
                )

                audioItem != null -> PlayButton(
                    audioItem = audioItem,
                    playbackState = playbackState,
                    controller = controller,
                    modifier = Modifier.size(width = 80.dp, height = 56.dp),
                )

                else -> DisabledPlayButton(
                    modifier = Modifier.size(width = 80.dp, height = 56.dp),
                )
            }
            FilledTonalIconButton(
                onClick = onNextButtonClick,
                shape = IconButtonDefaults.smallSquareShape,
                modifier = Modifier.size(56.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = stringResource(Res.string.content_description_next),
                )
            }

        }

    }
}

@Composable
private fun PlayButton(
    audioItem: AudioItem,
    playbackState: PlaybackState,
    controller: PlaybackController?,
    modifier: Modifier = Modifier,
) {
    LaunchedEffect(audioItem, playbackState.mediaId) {
        // Decide if we need to play the new track automatically.
        // For example, the user swipes to the next page while the current song is still playing.
        if (audioItem.mediaId != playbackState.mediaId) {
            controller?.run {
                if (playbackState.isPlaying) {
                    playNew(audioItem)
                } else if (playbackState.playerState != PlayerState.Idle) {
                    prepareNew(audioItem)
                }
            }
        }
    }
    AnimatedVisibility(
        visible = controller != null,
        modifier = modifier,
    ) {
        FilledIconButton(
            onClick = {
                controller?.run {
                    when (playbackState.playerState) {
                        PlayerState.Idle -> {
                            playNew(audioItem)
                        }

                        PlayerState.Ended -> {
                            seekTo(position = 0)
                            play()
                        }

                        PlayerState.Ready -> {
                            if (playbackState.isPlaying) pause() else play()
                        }

                        PlayerState.Buffering -> {
                            // Buffering
                        }
                    }
                }
            },
            shape = IconButtonDefaults.mediumSquareShape,
            modifier = Modifier.fillMaxSize().padding(2.dp),
        ) {
            Icon(
                imageVector = if (playbackState.isPlaying) {
                    Icons.Default.Pause
                } else {
                    Icons.Rounded.PlayArrow
                },
                contentDescription = if (playbackState.isPlaying) {
                    stringResource(Res.string.content_description_pause)
                } else {
                    stringResource(Res.string.content_description_play)
                },
            )
        }
    }
}

@Composable
private fun DisabledPlayButton(modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        FilledIconButton(
            onClick = {},
            enabled = false,
            modifier = Modifier.fillMaxSize(),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
        ) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = stringResource(Res.string.content_description_media_not_available),
            )
        }
    }
}

private fun PlaybackController.playNew(audioItem: AudioItem) {
    pause()
    prepareNew(audioItem)
    playWhenReady()
}

private fun PlaybackController.prepareNew(audioItem: AudioItem) {
    setItems(persistentListOf(audioItem))
    prepare()
    seekTo(position = 0)
}
