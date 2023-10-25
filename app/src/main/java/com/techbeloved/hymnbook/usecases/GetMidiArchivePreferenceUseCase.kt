package com.techbeloved.hymnbook.usecases

import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.techbeloved.hymnbook.utils.AssetsConstants
import javax.inject.Inject

class GetMidiArchivePreferenceUseCase @Inject constructor(private val rxPreferences: RxSharedPreferences) {

    operator fun invoke(): Set<String> = rxPreferences.getStringSet(AssetsConstants.PREF_MIDI_ARCHIVE_ASSETS).get()

}
