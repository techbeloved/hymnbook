package com.techbeloved.hymnbook.shared.appversion

import com.techbeloved.hymnbook.shared.di.AndroidInjector

internal actual val defaultAppVersionProvider: AppVersionProvider = AppVersionProvider {
    checkNotNull(AndroidInjector.appVersion)
}
