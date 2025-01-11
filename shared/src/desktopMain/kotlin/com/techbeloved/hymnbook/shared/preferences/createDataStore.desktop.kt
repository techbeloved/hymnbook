package com.techbeloved.hymnbook.shared.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.techbeloved.hymnbook.shared.files.defaultOkioFileSystemProvider

internal actual fun createPlatformDataStore(): DataStore<Preferences> = createDataStore {
    defaultOkioFileSystemProvider.get().userData.resolve(dataStoreFileName).toString()
}
