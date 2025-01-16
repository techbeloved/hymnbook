package com.techbeloved.hymnbook.shared.preferences

import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

internal class GetSongPreferenceFlowUseCase(
    private val repository: PreferencesRepository = Injector.preferencesRepository,
) {
    operator fun invoke(): Flow<SongPreferences> = combine(
        repository.getPreferenceFlow(SongPreferences.songDisplayModePrefKey),
        repository.getPreferenceFlow(SongPreferences.isPreferMidiPrefKey),
        repository.getPreferenceFlow(SongPreferences.songFontSizePrefKey),
    ) { displayMode, isPreferMidi, fontSize ->
        SongPreferences(
            songDisplayMode = SongDisplayMode.valueOf(displayMode),
            isPreferMidi = isPreferMidi,
            fontSize = fontSize,
        )
    }
}
