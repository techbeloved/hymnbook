@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbeloved.hymnbook.shared.ext.percentToNearestFive
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.preferences.SongPreferences
import hymnbook.shared.generated.resources.Res
import hymnbook.shared.generated.resources.now_playing_add_to_playlist
import hymnbook.shared.generated.resources.now_playing_settings_loop
import hymnbook.shared.generated.resources.now_playing_settings_lyrics_size
import hymnbook.shared.generated.resources.now_playing_settings_music_speed
import hymnbook.shared.generated.resources.now_playing_settings_speed_down
import hymnbook.shared.generated.resources.now_playing_settings_speed_up
import hymnbook.shared.generated.resources.now_playing_settings_zoom_in
import hymnbook.shared.generated.resources.now_playing_settings_zoom_out
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NowPlayingSettingsBottomSheet(
    onDismiss: () -> Unit,
    onSpeedUp: () -> Unit,
    onSpeedDown: () -> Unit,
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    onChangeSongDisplayMode: (songDisplayMode: SongDisplayMode) -> Unit,
    onAddSongToPlaylist: () -> Unit,
    onToggleLooping: (isLooping: Boolean) -> Unit,
    isLooping: Boolean,
    isLoopingSupported: Boolean,
    preferences: SongPreferences,
    playbackSpeed: Int,
    modifier: Modifier = Modifier,
    bottomSheetState: SheetState = rememberModalBottomSheetState(),
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.CenterHorizontally,
            )
        ) {
            FilledTonalButton(
                onClick = onAddSongToPlaylist,
            ) {
                Row {
                    Text(text = stringResource(Res.string.now_playing_add_to_playlist))
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.TwoTone.PlaylistAdd,
                        contentDescription = stringResource(Res.string.now_playing_add_to_playlist),
                    )
                }
            }
        }
        SheetMusicToggle(
            songDisplayMode = preferences.songDisplayMode,
            onToggle = onChangeSongDisplayMode,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        if (isLoopingSupported) {
            HorizontalDivider()
            LoopingControls(
                isLooping = isLooping,
                onToggleLooping = onToggleLooping,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
            )
        }
        HorizontalDivider()
        MusicSpeedControls(
            currentSpeed = playbackSpeed,
            onSpeedUp = onSpeedUp,
            onSpeedDown = onSpeedDown,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        )
        HorizontalDivider()
        ZoomButtons(
            onZoomIn = onZoomIn,
            onZoomOut = onZoomOut,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = preferences.fontSize.sp,
        )
    }
}

@Composable
private fun LoopingControls(
    isLooping: Boolean,
    onToggleLooping: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsControl(
        text = stringResource(Res.string.now_playing_settings_loop),
        modifier = modifier,
    ) {
        Switch(
            checked = isLooping,
            onCheckedChange = onToggleLooping,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Composable
private fun ZoomButtons(
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    fontSize: TextUnit,
    modifier: Modifier = Modifier,
) {
    SettingsControl(
        modifier = modifier,
        text = stringResource(Res.string.now_playing_settings_lyrics_size),
    ) {
        IconButton(
            onClick = onZoomOut,
            modifier = Modifier.align(Alignment.CenterStart),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(Res.string.now_playing_settings_zoom_out),
            )
        }
        Text(
            text = "A",
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.Center),
        )
        IconButton(
            onClick = onZoomIn,
            modifier = Modifier.align(Alignment.CenterEnd),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.now_playing_settings_zoom_in),
            )
        }
    }
}

@Composable
private fun MusicSpeedControls(
    currentSpeed: Int,
    onSpeedUp: () -> Unit,
    onSpeedDown: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsControl(
        modifier = modifier,
        text = stringResource(Res.string.now_playing_settings_music_speed),
    ) {
        IconButton(
            onClick = onSpeedDown,
            modifier = Modifier.align(Alignment.CenterStart),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = stringResource(Res.string.now_playing_settings_speed_down),
            )
        }
        Text(
            text = "${currentSpeed.percentToNearestFive}×",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.Center),
        )
        IconButton(
            onClick = onSpeedUp,
            modifier = Modifier.align(Alignment.CenterEnd),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(Res.string.now_playing_settings_speed_up),
            )
        }
    }
}

@Composable
private fun SheetMusicToggle(
    songDisplayMode: SongDisplayMode,
    onToggle: (songDisplayMode: SongDisplayMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsControl(
        text = "Sheet Music",
        modifier = modifier,
    ) {
        Switch(
            checked = songDisplayMode == SongDisplayMode.SheetMusic,
            onCheckedChange = { isChecked ->
                onToggle(if (isChecked) SongDisplayMode.SheetMusic else SongDisplayMode.Lyrics)
            },
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Composable
private fun SettingsControl(
    text: String,
    modifier: Modifier = Modifier,
    controls: @Composable BoxScope.() -> Unit,
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(text = text, modifier = Modifier.weight(weight = 1f))

        Box(
            Modifier.weight(weight = 1f),
        ) {
            controls()
        }
    }
}
