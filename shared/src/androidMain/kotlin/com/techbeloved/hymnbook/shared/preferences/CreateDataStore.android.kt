package com.techbeloved.hymnbook.shared.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.techbeloved.hymnbook.shared.di.AndroidInjector

internal fun createDataStore(context: Context): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve(DataStoreFileName).absolutePath }
)

internal actual fun createPlatformDataStore(): DataStore<Preferences> =
    createDataStore(AndroidInjector.application)
