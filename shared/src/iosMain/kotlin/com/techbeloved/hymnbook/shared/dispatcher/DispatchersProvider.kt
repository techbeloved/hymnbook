package com.techbeloved.hymnbook.shared.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
internal actual class DispatchersProvider {
    actual fun main(): CoroutineDispatcher = Dispatchers.Main

    actual fun io(): CoroutineDispatcher = Dispatchers.IO

    actual fun default(): CoroutineDispatcher = Dispatchers.Default
}

internal actual fun getPlatformDispatcherProvider(): DispatchersProvider = DispatchersProvider()