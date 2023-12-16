package com.techbeloved.hymnbook.shared.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newFixedThreadPoolContext
internal actual class DispatchersProvider {
    actual fun main(): CoroutineDispatcher = Dispatchers.Main

    actual fun io(): CoroutineDispatcher = newFixedThreadPoolContext(nThreads = 200, name = "IO")

    actual fun default(): CoroutineDispatcher = Dispatchers.Default
}

internal actual fun getPlatformDispatcherProvider(): DispatchersProvider = DispatchersProvider()