package com.techbeloved.hymnbook.shared.appversion

import platform.Foundation.NSBundle

internal actual val defaultAppVersionProvider: AppVersionProvider = AppVersionProvider {
    val buildNumber = NSBundle.mainBundle.infoDictionary?.get("CFBundleVersion")?.toString()
        .orEmpty()
    val versionCode =
        NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString")?.toString().orEmpty()
    AppVersion(name = versionCode, code = buildNumber)
}
