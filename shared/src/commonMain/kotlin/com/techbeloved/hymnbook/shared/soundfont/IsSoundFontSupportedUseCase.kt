package com.techbeloved.hymnbook.shared.soundfont

import me.tatarka.inject.annotations.Inject

internal class IsSoundFontSupportedUseCase @Inject constructor() {

    operator fun invoke() = IsSoundFontSupported
}
