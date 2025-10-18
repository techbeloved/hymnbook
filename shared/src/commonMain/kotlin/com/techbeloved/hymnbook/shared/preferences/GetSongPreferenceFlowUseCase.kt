package com.techbeloved.hymnbook.shared.preferences

import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.tatarka.inject.annotations.Inject

internal class GetSongPreferenceFlowUseCase @Inject constructor(
    private val repository: PreferencesRepository,
) {
    operator fun invoke(): Flow<SongPreferences> = combine(
        repository.getPreferenceFlow(SongPreferences.songDisplayModePrefKey),
        repository.getPreferenceFlow(SongPreferences.isPreferMidiPrefKey),
        repository.getPreferenceFlow(SongPreferences.songFontSizePrefKey),
        repository.getPreferenceFlow(SongPreferences.songCompactDisplayPrefKey),
    ) { displayMode, isPreferMidi, fontSize, isCompactDisplay ->
        SongPreferences(
            songDisplayMode = SongDisplayMode.valueOf(displayMode),
            isPreferMidi = isPreferMidi,
            fontSize = fontSize,
            isCompactDisplay = isCompactDisplay,
        )
    }
}
