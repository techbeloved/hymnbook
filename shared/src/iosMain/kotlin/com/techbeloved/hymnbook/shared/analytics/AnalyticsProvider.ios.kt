package com.techbeloved.hymnbook.shared.analytics

import com.techbeloved.hymnbook.shared.di.IosInjector

internal actual fun analyticsProvider(): AppAnalytics = IosInjector.analytics
