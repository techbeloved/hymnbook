package com.techbeloved.hymnbook.shared.analytics

import com.techbeloved.hymnbook.shared.di.AndroidInjector

internal actual fun analyticsProvider(): AppAnalytics = AndroidInjector.analytics
