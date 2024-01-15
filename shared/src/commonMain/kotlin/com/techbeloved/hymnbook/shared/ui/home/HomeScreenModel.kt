package com.techbeloved.hymnbook.shared.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.files.ExtractArchiveUseCase
import com.techbeloved.hymnbook.shared.files.HashAssetFileUseCase
import com.techbeloved.hymnbook.shared.files.defaultOkioFileSystemProvider
import com.techbeloved.hymnbook.shared.model.HymnItem
import com.techbeloved.hymnbook.shared.model.ext.OpenLyricsSong
import com.techbeloved.hymnbook.shared.openlyrics.ImportOpenLyricsUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.plus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

internal class HomeScreenModel(
    private val hashAssetFileUseCase: HashAssetFileUseCase = HashAssetFileUseCase(),
    private val extractArchiveUseCase: ExtractArchiveUseCase = ExtractArchiveUseCase(),
    private val importOpenLyricsUseCase: ImportOpenLyricsUseCase = ImportOpenLyricsUseCase(),
) : ScreenModel {
    val state: MutableStateFlow<ImmutableList<HymnItem>> = MutableStateFlow(persistentListOf())

    init {
        screenModelScope.launch {

            val assetFileHash = hashAssetFileUseCase("assets/openlyrics/ten_thousand_reason.xml")
            val item1 = HymnItem(id = 1, title = "Asset file", subtitle = "${assetFileHash.sha256}, path: ${assetFileHash.path}")
            state.value = persistentListOf(item1) + sampleHymnItems
            val fileSystem = defaultOkioFileSystemProvider.get()
            val lyricsDir =  fileSystem.tempDir / "lyrics/"
            fileSystem.fileSystem.createDirectory(lyricsDir)

            val result = extractArchiveUseCase(assetFilePath = "assets/openlyrics/sample_songs.zip", destination = lyricsDir)
            if (result.isSuccess) {
                importOpenLyricsUseCase(lyricsDir)
            } else {
                result.exceptionOrNull()?.printStackTrace()
            }
        }
    }
    companion object {
        private val sampleHymnItems = persistentListOf(
            HymnItem(id = 4, title = "Hymn 4", subtitle = xmEncoding()),
            HymnItem(id = 5, title = "Hymn 5", subtitle = "Praise and worship"),
            HymnItem(id = 6, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 7, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 8, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 9, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 10, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 11, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 12, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 13, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 14, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 15, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 16, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 17, title = "Hymn 6", subtitle = "Praise in holiness"),
        )
    }
}

private val sampleOpenLyricsSongWithSongbook = OpenLyricsSong(
    createdIn = "Android Studio",
    properties = OpenLyricsSong.Properties(
        titles = listOf(
            OpenLyricsSong.Title("Song 1"),
            OpenLyricsSong.Title("Song of degrees"),
        ),
        songbooks = listOf(OpenLyricsSong.Songbook(name = "songbook1", entry = "1")),
        keywords = "Faith, believe, love, joy and peace",
        comments = listOf("I want to know him", "I love my master 1999"),
        authors = listOf(
            OpenLyricsSong.Author("John Newton", "lyrics", "published 1855"),
            OpenLyricsSong.Author("Edward Snowden", "music", "published 1999"),
        ),
        verseOrder = "v1 c1",
        themes = listOf(OpenLyricsSong.Theme("Worship")),
    ),
    lyrics = listOf(
        OpenLyricsSong.Verse(
            name = "v1",
            lines = listOf(OpenLyricsSong.Lines("Line 1\nLine 2")),
        ),
        OpenLyricsSong.Verse(
            name = "v2",
            lines = listOf(
                OpenLyricsSong.Lines("Line 3\nLine 4", part = "men"),
                OpenLyricsSong.Lines("Line 3\nLine 4", part = "women"),
            ),
        ),
    )
)

private fun xmEncoding(): String {

    return Injector.xml.encodeToString(
        OpenLyricsSong.serializer(),
        sampleOpenLyricsSongWithSongbook
    )
}
