package com.techbeloved.hymnbook.shared.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

internal class DispatchersProvider {

    fun io(): CoroutineDispatcher = Dispatchers.IO

    fun default(): CoroutineDispatcher = Dispatchers.Default
}

internal fun getPlatformDispatcherProvider(): DispatchersProvider = DispatchersProvider()
