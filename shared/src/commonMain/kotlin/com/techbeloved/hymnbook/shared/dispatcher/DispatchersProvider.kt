package com.techbeloved.hymnbook.shared.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

internal expect class DispatchersProvider {
    fun main(): CoroutineDispatcher

    fun io(): CoroutineDispatcher

    fun default(): CoroutineDispatcher
}

internal expect fun getPlatformDispatcherProvider(): DispatchersProvider