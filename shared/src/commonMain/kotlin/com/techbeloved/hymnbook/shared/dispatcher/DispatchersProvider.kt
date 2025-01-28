package com.techbeloved.hymnbook.shared.dispatcher

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Inject

internal class DispatchersProvider @Inject constructor() {

    fun io(): CoroutineDispatcher = Dispatchers.IO

    fun default(): CoroutineDispatcher = Dispatchers.Default
}
