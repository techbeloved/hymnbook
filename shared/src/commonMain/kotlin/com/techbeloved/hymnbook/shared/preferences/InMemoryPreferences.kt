package com.techbeloved.hymnbook.shared.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

internal class InMemoryPreferences(private val store: MutableMap<String, Any?> = mutableMapOf()) {

    operator fun <T> set(key: Preferences.Key<T>, value: T?) {
        store[key.name] = value
    }

    operator fun <T> get(key: Preferences.Key<T>): T? = store[key.name] as? T

    override fun equals(other: Any?): Boolean {
        if (other !is InMemoryPreferences) return false

        if (other.store.size != store.size) return false

        return other.store.all { otherEntry ->
            store[otherEntry.key]?.let { value ->
                when (val otherVal = otherEntry.value) {
                    is ByteArray -> value is ByteArray && otherVal.contentEquals(value)
                    else -> otherVal == value
                }
            } ?: false
        }
    }

    override fun hashCode(): Int {
        return store.entries.sumOf { entry ->
            when (val value = entry.value) {
                is ByteArray -> value.contentHashCode()
                else -> value.hashCode()
            }
        }
    }

    fun toMutablePreference(): InMemoryPreferences = InMemoryPreferences(store.toMutableMap())
}

/**
 * Updates the InMemoryPreferences DataStore atomically
 */
internal suspend fun DataStore<InMemoryPreferences>.edit(
    transform: suspend (InMemoryPreferences) -> Unit,
): InMemoryPreferences {
    return this.updateData {
        it.toMutablePreference().apply { transform(this) }
    }
}
