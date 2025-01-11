package com.techbeloved.hymnbook.shared.preferences

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet

internal class InMemoryDataStore : DataStore<InMemoryPreferences> {
    private val mutableStateFlow = MutableStateFlow(InMemoryPreferences())

    override val data: Flow<InMemoryPreferences> = mutableStateFlow.asStateFlow()

    override suspend fun updateData(
        transform: suspend (original: InMemoryPreferences) -> InMemoryPreferences,
    ): InMemoryPreferences = mutableStateFlow.updateAndGet { transform(it) }
}
