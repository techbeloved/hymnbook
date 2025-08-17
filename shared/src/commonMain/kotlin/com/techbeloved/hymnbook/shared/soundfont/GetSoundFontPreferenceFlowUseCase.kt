package com.techbeloved.hymnbook.shared.soundfont

import com.techbeloved.hymnbook.shared.preferences.GetPreferenceFlowUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.mapLatest
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalCoroutinesApi::class)
internal class GetSoundFontPreferenceFlowUseCase @Inject constructor(
    private val getPreferenceFlowUseCase: GetPreferenceFlowUseCase,
    private val getSavedSoundFontByNameUseCase: GetSavedSoundFontByNameUseCase,
) {
    operator fun invoke() =
        getPreferenceFlowUseCase(SoundFontPreferenceKey).mapLatest { preference ->
            if (preference.isNotBlank()) {
                getSavedSoundFontByNameUseCase(preference)
            } else {
                null
            }
        }
}
