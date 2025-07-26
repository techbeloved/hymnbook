package com.techbeloved.hymnbook.shared.soundfont

import com.techbeloved.hymnbook.shared.generated.Res

internal actual fun soundFontProvider(): String? = Res.getUri("files/soundfont/soundfont.sf2")
