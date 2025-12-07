@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)

package com.techbeloved.hymnbook.shared.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SettingsInputComponent
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.techbeloved.hymnbook.shared.ext.percentToNearestFive
import com.techbeloved.hymnbook.shared.generated.Res
import com.techbeloved.hymnbook.shared.generated.ic_lyrics_compact
import com.techbeloved.hymnbook.shared.generated.now_playing_add_to_playlist
import com.techbeloved.hymnbook.shared.generated.now_playing_settings_loop
import com.techbeloved.hymnbook.shared.generated.now_playing_settings_lyrics_size
import com.techbeloved.hymnbook.shared.generated.now_playing_settings_music_speed
import com.techbeloved.hymnbook.shared.generated.now_playing_settings_speed_down
import com.techbeloved.hymnbook.shared.generated.now_playing_settings_speed_up
import com.techbeloved.hymnbook.shared.generated.now_playing_settings_zoom_in
import com.techbeloved.hymnbook.shared.generated.now_playing_settings_zoom_out
import com.techbeloved.hymnbook.shared.generated.now_playing_share_action
import com.techbeloved.hymnbook.shared.generated.now_playing_soundfont
import com.techbeloved.hymnbook.shared.generated.now_playing_soundfont_description
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.preferences.SongPreferences
import com.techbeloved.hymnbook.shared.settings.DarkModePreference
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NowPlayingSettingsBottomSheet(
    onDismiss: () -> Unit,
    onSpeedUp: () -> Unit,
    onSpeedDown: () -> Unit,
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    onChangeSongDisplayMode: (songDisplayMode: SongDisplayMode) -> Unit,
    onSoundfonts: () -> Unit,
    onAddSongToPlaylist: () -> Unit,
    onToggleLooping: (isLooping: Boolean) -> Unit,
    isSoundfontSupported: Boolean,
    isLooping: Boolean,
    isLoopingSupported: Boolean,
    preferences: SongPreferences,
    playbackSpeed: Int,
    onShareSongClick: () -> Unit,
    darkModePreference: DarkModePreference,
    onToggleDarkMode: (DarkModePreference) -> Unit,
    modifier: Modifier = Modifier,
    bottomSheetState: SheetState = rememberModalBottomSheetState(),
) {
    val localScope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = bottomSheetState,
        modifier = modifier,
    ) {
        SongDisplayModeSection(
            songDisplayMode = preferences.songDisplayMode,
            onToggle = onChangeSongDisplayMode,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        LyricsAppearanceSection(
            onZoomOut = onZoomOut,
            onZoomIn = onZoomIn,
            preferences = preferences,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            darkModePreference = darkModePreference,
            onToggleDarkMode = onToggleDarkMode,
        )
        AudioControlsSection(
            onSpeedUp = onSpeedUp,
            onSpeedDown = onSpeedDown,
            onSoundfonts = onSoundfonts,
            onToggleLooping = onToggleLooping,
            isSoundfontSupported = isSoundfontSupported,
            isLooping = isLooping,
            isLoopingSupported = isLoopingSupported,
            playbackSpeed = playbackSpeed,
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        ActionsSection(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            onShare = {
                localScope.launch {
                    bottomSheetState.hide()
                    onShareSongClick()
                    onDismiss()
                }
            },
            onAddSongToPlaylist = onAddSongToPlaylist,
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
        OutlinedIconButton(
            onClick = onZoomOut,
            modifier = Modifier.align(Alignment.CenterStart),
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = stringResource(Res.string.now_playing_settings_zoom_out),
            )
        }
        Text(
            text = "Aa",
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.align(Alignment.Center),
        )
        FilledIconButton(
            onClick = onZoomIn,
            modifier = Modifier.align(Alignment.CenterEnd),
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
        OutlinedIconButton(
            onClick = onSpeedDown,
            modifier = Modifier.align(Alignment.CenterStart),
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
        FilledIconButton(
            onClick = onSpeedUp,
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = stringResource(Res.string.now_playing_settings_speed_up),
            )
        }
    }
}

@Composable
private fun SongDisplayModeSection(
    songDisplayMode: SongDisplayMode,
    onToggle: (songDisplayMode: SongDisplayMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = "View", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Surface(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small) {
            SettingsToggleRow(
                selected = songDisplayMode,
                items = SongDisplayMode.entries,
                title = { it.title },
                icon = { it.Icon() },
                onToggle = onToggle,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private inline fun <reified T> SettingsToggleRow(
    selected: T,
    items: List<T>,
    crossinline title: (T) -> String,
    crossinline onToggle: (setting: T) -> Unit,
    crossinline icon: @Composable (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.padding(horizontal = 8.dp, vertical = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
        verticalArrangement = Arrangement.spacedBy(2.dp),
        maxItemsInEachRow = 3,
    ) {
        items.forEachIndexed { index, item ->
            ToggleButton(
                checked = selected == item,
                onCheckedChange = { isChecked ->
                    if (isChecked) {
                        onToggle(item)
                    }
                },
                modifier = Modifier.weight(1f)
                    .semantics { role = Role.RadioButton },
                shapes = when (index) {
                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                    SongDisplayMode.entries.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                    else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                },
                contentPadding = PaddingValues(4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    icon(item)
                    Text(
                        text = title(item),
                        maxLines = 1,
                        style = MaterialTheme.typography.bodyMediumEmphasized,
                    )
                }
            }
        }

    }
}

@Composable
private fun SongDisplayMode.Icon() = when (this) {
    SongDisplayMode.Lyrics -> Icon(
        imageVector = Icons.Default.Lyrics,
        contentDescription = null,
        modifier = Modifier.size(24.dp),
    )

    SongDisplayMode.LyricsCompact -> Icon(
        painter = painterResource(Res.drawable.ic_lyrics_compact),
        contentDescription = null,
        modifier = Modifier.size(24.dp),
    )

    SongDisplayMode.SheetMusic -> Icon(
        imageVector = Icons.Default.MusicNote,
        contentDescription = null,
        modifier = Modifier.size(24.dp),
    )
}

@Composable
private fun LyricsAppearanceSection(
    onZoomOut: () -> Unit,
    onZoomIn: () -> Unit,
    preferences: SongPreferences,
    darkModePreference: DarkModePreference,
    onToggleDarkMode: (DarkModePreference) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = "Appearance", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        Surface(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small) {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                    .fillMaxWidth(),
            ) {
                ZoomButtons(
                    onZoomIn = onZoomIn,
                    onZoomOut = onZoomOut,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = MaterialTheme.typography.bodySmall.fontSize * preferences.fontSize,
                )
                SettingsToggleRow(
                    selected = darkModePreference,
                    items = DarkModePreference.entries,
                    title = { it.name },
                    onToggle = onToggleDarkMode,
                    icon = { it.Icon() },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Composable
private fun DarkModePreference.Icon() {
    val icon = when (this) {
        DarkModePreference.Light -> Icons.Default.LightMode
        DarkModePreference.Dark -> Icons.Default.DarkMode
        DarkModePreference.System -> Icons.Default.Nightlight
    }

    Icon(imageVector = icon, contentDescription = null)
}

@Composable
private fun AudioControlsSection(
    onSpeedUp: () -> Unit,
    onSpeedDown: () -> Unit,
    onSoundfonts: () -> Unit,
    onToggleLooping: (isLooping: Boolean) -> Unit,
    isSoundfontSupported: Boolean,
    isLooping: Boolean,
    isLoopingSupported: Boolean,
    playbackSpeed: Int,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(text = "Audio", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Surface(modifier = Modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small) {
            Column(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                    .fillMaxWidth(),
            ) {
                if (isLoopingSupported) {
                    LoopingControls(
                        isLooping = isLooping,
                        onToggleLooping = onToggleLooping,
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                    )
                    HorizontalDivider()
                }

                MusicSpeedControls(
                    currentSpeed = playbackSpeed,
                    onSpeedUp = onSpeedUp,
                    onSpeedDown = onSpeedDown,
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                )
                if (isSoundfontSupported) {
                    HorizontalDivider()
                    SettingsControl(
                        text = stringResource(Res.string.now_playing_soundfont),
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                    ) {
                        IconButton(
                            onClick = onSoundfonts,
                        ) {
                            Row {
                                Icon(
                                    imageVector = Icons.Default.SettingsInputComponent,
                                    contentDescription = stringResource(Res.string.now_playing_soundfont_description),
                                )
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
private fun ActionsSection(
    modifier: Modifier = Modifier,
    onShare: () -> Unit,
    onAddSongToPlaylist: () -> Unit,
) {
    Surface(modifier = modifier.fillMaxWidth(), shape = MaterialTheme.shapes.small) {
        FlowRow(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            maxItemsInEachRow = 3,
        ) {
            SettingsActionButton(
                title = stringResource(Res.string.now_playing_share_action),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = stringResource(Res.string.now_playing_share_action),
                    )
                },
                onClick = onShare,
                modifier = Modifier.weight(1f),
            )
            SettingsActionButton(
                title = stringResource(Res.string.now_playing_add_to_playlist),
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                        contentDescription = stringResource(Res.string.now_playing_add_to_playlist),
                    )
                },
                onClick = onAddSongToPlaylist,
                modifier = Modifier.weight(1f),
            )

        }
    }
}

@Composable
private fun SettingsActionButton(
    title: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        shapes = ButtonDefaults.shapes(),
        contentPadding = PaddingValues(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            icon()
            Text(
                text = title,
                maxLines = 1,
                style = MaterialTheme.typography.bodyMediumEmphasized,
            )
        }
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
            modifier = Modifier.weight(weight = 1f),
            contentAlignment = Alignment.CenterEnd,
        ) {
            controls()
        }
    }
}
