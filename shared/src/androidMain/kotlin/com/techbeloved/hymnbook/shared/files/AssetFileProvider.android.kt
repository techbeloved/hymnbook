package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.di.AndroidInjector
import okio.source

internal actual val assetFileSourceProvider: AssetFileSourceProvider = AssetFileSourceProvider { path ->
    openAndroidAsset(path).source()
}

internal fun openAndroidAsset(assetFilePath: String) = AndroidInjector.application.assets.open(
    assetFilePath.removePrefix("files/").removePrefix("file:///android_asset/")
)
