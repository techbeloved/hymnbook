package com.techbeloved.hymnbook.shared.di

import androidx.datastore.core.DataStore
import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.MediaFile
import com.techbeloved.hymnbook.PlaylistEntity
import com.techbeloved.hymnbook.SheetMusicEntity
import com.techbeloved.hymnbook.SongEntity
import com.techbeloved.hymnbook.shared.data.dateColumnAdapter
import com.techbeloved.hymnbook.shared.data.getDriverFactory
import com.techbeloved.hymnbook.shared.data.listColumnAdapter
import com.techbeloved.hymnbook.shared.preferences.InMemoryDataStore
import com.techbeloved.hymnbook.shared.preferences.InMemoryPreferences
import com.techbeloved.hymnbook.shared.preferences.createPlatformDataStore
import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.core.XmlVersion
import nl.adaptivity.xmlutil.serialization.XML

internal object Injector {

    val database: Database by lazy {
        val driver = getDriverFactory().createDriver()
        getDatabase(driver)
    }

    private val json: Json by lazy {
        Json {
            isLenient = true
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }

    val xml: XML by lazy {
        XML {
            xmlVersion = XmlVersion.XML10
            xmlDeclMode = XmlDeclMode.Auto
            indentString = "  "
            repairNamespaces = true
        }
    }

    val preferencesDataStore by lazy { createPlatformDataStore() }

    val inMemoryDataStore: DataStore<InMemoryPreferences> by lazy { InMemoryDataStore() }

    fun getDatabase(driver: SqlDriver): Database {
        return Database(
            driver = driver,
            MediaFileAdapter = MediaFile.Adapter(EnumColumnAdapter()),
            SheetMusicEntityAdapter = SheetMusicEntity.Adapter(EnumColumnAdapter()),
            SongEntityAdapter = SongEntity.Adapter(
                lyricsAdapter = listColumnAdapter(json),
                createdAdapter = dateColumnAdapter(),
                modifiedAdapter = dateColumnAdapter(),
            ),
            PlaylistEntityAdapter = PlaylistEntity.Adapter(
                createdAdapter = dateColumnAdapter(),
                modifiedAdapter = dateColumnAdapter(),
            ),
        )
    }
}
