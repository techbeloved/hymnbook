package com.techbeloved.hymnbook.shared.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.files.AssetArchiveExtractor
import com.techbeloved.hymnbook.shared.files.AssetFileSourceProvider
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.files.assetFileSourceProvider
import com.techbeloved.hymnbook.shared.files.defaultAssetArchiveExtractor
import com.techbeloved.hymnbook.shared.files.defaultOkioFileSystemProvider
import com.techbeloved.hymnbook.shared.preferences.InMemoryPreferences
import com.techbeloved.hymnbook.shared.time.DefaultInstantProvider
import com.techbeloved.hymnbook.shared.time.InstantProvider
import com.techbeloved.hymnbook.shared.ui.detail.SongDetailPagerModel
import com.techbeloved.hymnbook.shared.ui.detail.SongDetailScreenModel
import com.techbeloved.hymnbook.shared.ui.home.HomeScreenModel
import com.techbeloved.hymnbook.shared.ui.playlist.PlaylistsViewModel
import com.techbeloved.hymnbook.shared.ui.playlist.add.AddEditPlaylistViewModel
import com.techbeloved.hymnbook.shared.ui.playlist.select.AddSongToPlaylistViewModel
import com.techbeloved.hymnbook.shared.ui.search.SearchScreenModel
import com.techbeloved.hymnbook.shared.ui.songs.FilteredSongsViewModel
import com.techbeloved.hymnbook.shared.ui.topics.TopicsViewModel
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Component
@SingleIn(AppScope::class)
internal interface AppComponent {

    fun filteredSongsViewModelFactory(): FilteredSongsViewModel.Factory

    fun detailScreenModelFactory(): SongDetailScreenModel.Factory

    fun detailPagerScreenModelFactory(): SongDetailPagerModel.Factory

    fun homeScreenModel(): HomeScreenModel

    fun searchScreenModel(): SearchScreenModel

    fun topicsViewModel(): TopicsViewModel

    fun playlistsViewModel(): PlaylistsViewModel

    @Provides
    fun assetFileSource(): AssetFileSourceProvider = assetFileSourceProvider

    @Provides
    fun assetArchiveExtractor(): AssetArchiveExtractor = defaultAssetArchiveExtractor

    @Provides
    fun okioFileSystem(): OkioFileSystemProvider = defaultOkioFileSystemProvider

    @Provides
    fun providePersistentPreferenceDataStore(): DataStore<Preferences> =
        Injector.preferencesDataStore

    @Provides
    fun provideInMemoryPreferenceDataStore(): DataStore<InMemoryPreferences> =
        Injector.inMemoryDataStore

    @Provides
    fun database(): Database = Injector.database

    @Provides
    fun json(): Json = Injector.json

    @Provides
    fun provideInstantProvider(instantProvider: DefaultInstantProvider): InstantProvider =
        instantProvider
    fun addNewPlaylistViewModelFactory(): AddEditPlaylistViewModel.Factory
    fun addSongToPlaylistViewModelFactory(): AddSongToPlaylistViewModel.Factory

    companion object
}

@KmpComponentCreate
internal expect fun AppComponent.Companion.create(): AppComponent

internal val appComponent by lazy { AppComponent.create() }
