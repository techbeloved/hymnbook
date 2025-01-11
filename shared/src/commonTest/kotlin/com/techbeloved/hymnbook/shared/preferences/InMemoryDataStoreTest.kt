package com.techbeloved.hymnbook.shared.preferences

import androidx.datastore.preferences.core.intPreferencesKey
import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class InMemoryDataStoreTest {

    private val inMemoryDataStore = InMemoryDataStore()

    @Test
    fun testUpdatesAreReflected() = runTest{
        inMemoryDataStore.data.test {
            assertEquals(
                expected = InMemoryPreferences(),
                actual = awaitItem(),
            )
            inMemoryDataStore.edit {
                it[intPreferencesKey("A")] = 1
            }

            assertEquals(
                expected = InMemoryPreferences(mutableMapOf("A" to 1)),
                actual = awaitItem(),
            )
        }

    }
}

