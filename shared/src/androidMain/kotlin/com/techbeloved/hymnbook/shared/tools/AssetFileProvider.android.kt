package com.techbeloved.hymnbook.shared.tools

import com.techbeloved.hymnbook.shared.di.AndroidInjector
import okio.source

internal actual val assetFileProvider: AssetFileProvider =  AssetFileProvider { path ->
    AndroidInjector.application.assets.open(
        path.removePrefix("assets/")
    ).source()
}
