@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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

@Composable
internal fun NowPlayingSettingsBottomSheet(
    onDismiss: () -> Unit,
    onSpeedUp: () -> Unit,
    onSpeedDown: () -> Unit,
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    onChangeSongDisplayMode: (songDisplayMode: SongDisplayMode) -> Unit,
    onAddSongToPlaylist: () -> Unit,
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
                    Text(text = "Add to playlist")
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.TwoTone.PlaylistAdd,
                        contentDescription = "Add to playlist",
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
        HorizontalDivider()
        ZoomButtons(
            onZoomIn = onZoomIn,
            onZoomOut = onZoomOut,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = preferences.fontSize.sp,
        )
        HorizontalDivider()
        MusicSpeedControls(
            currentSpeed = playbackSpeed,
            onSpeedUp = onSpeedUp,
            onSpeedDown = onSpeedDown,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
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
    SettingsControl(modifier = modifier, text = "Lyrics Size") {
        IconButton(onClick = onZoomOut) {
            Icon(Icons.Default.Remove, contentDescription = "Zoom out")
        }
        Text(
            text = "A",
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
        )
        IconButton(onClick = onZoomIn) {
            Icon(Icons.Default.Add, contentDescription = "Zoom in")
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
    SettingsControl(modifier = modifier, text = "Music Speed") {
        IconButton(onClick = onSpeedDown) {
            Icon(Icons.Default.Remove, contentDescription = "Speed down")
        }
        Text(
            text = "${currentSpeed.percentToNearestFive}X",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
        )
        IconButton(onClick = onSpeedUp) {
            Icon(Icons.Default.Add, contentDescription = "Speed up")
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
        )
    }
}

@Composable
private fun SettingsControl(
    text: String,
    modifier: Modifier = Modifier,
    controls: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Text(text = text)

        Spacer(Modifier.weight(1f))

        controls()
    }
}
