package com.techbeloved.hymnbook.shared.openlyrics

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.deleteAll
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.SongTitle
import com.techbeloved.hymnbook.shared.model.ext.OpenLyricsSong
import com.techbeloved.hymnbook.shared.testDatabaseDriver
import com.techbeloved.hymnbook.shared.titles.GetHymnTitlesUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class SaveOpenLyricsUseCaseTest {
    private lateinit var useCase: SaveOpenLyricsUseCase
    private lateinit var database: Database
    private lateinit var getHymnTitlesUseCase: GetHymnTitlesUseCase

    @BeforeTest
    fun setUp() {
        database = Injector.getDatabase(testDatabaseDriver())
        val dispatchersProvider = DispatchersProvider()
        getHymnTitlesUseCase = GetHymnTitlesUseCase(database, dispatchersProvider = dispatchersProvider)
        useCase = SaveOpenLyricsUseCase(
            database,
            instantProvider = { Instant.parse("2023-01-01T00:00:00Z") },
            dispatchersProvider = dispatchersProvider
        )
    }

    @AfterTest
    fun tearDown() {
        database.deleteAll()
    }

    @Test
    fun `Given an empty database_When a single song is inserted_Then allTitles returns the single song inserted`() =
        runTest {
            assertEquals(expected = emptyList(), actual = getHymnTitlesUseCase())
            useCase(sampleOpenLyricsSong)
            assertEquals(
                expected = listOf(
                    SongTitle(
                        id = 1L,
                        title = "Song 1",
                        alternateTitle = null,
                        songbook = null,
                        songbookEntry = null
                    )
                ),
                actual = getHymnTitlesUseCase(),
            )
        }

    @Test
    fun `Given an empty database_When a single song with songbook is inserted_Then allTitles returns the song`() =
        runTest {
            assertEquals(expected = emptyList(), actual = getHymnTitlesUseCase())
            useCase(sampleOpenLyricsSongWithSongbook)
            assertEquals(
                expected = listOf(
                    SongTitle(
                        id = 1L,
                        title = "Song 1",
                        alternateTitle = null,
                        songbook = "songbook1",
                        songbookEntry = "1"
                    )
                ),
                actual = getHymnTitlesUseCase(),
            )
        }

    @Test
    fun `Given an empty database_When a two songs are inserted_Then allTitles returns the all songs`() =
        runTest {
            assertEquals(expected = emptyList(), actual = getHymnTitlesUseCase())
            useCase(sampleOpenLyricsSongWithSongbook)
            useCase(sampleOpenLyricsSong)
            assertEquals(
                expected = listOf(
                    SongTitle(
                        id = 1L,
                        title = "Song 1",
                        alternateTitle = null,
                        songbook = "songbook1",
                        songbookEntry = "1",
                    ),
                    SongTitle(
                        id = 2L,
                        title = "Song 1",
                        alternateTitle = null,
                        songbook = null,
                        songbookEntry = null,
                    ),
                ),
                actual = getHymnTitlesUseCase(),
            )
        }

    companion object {
        private val sampleOpenLyricsSong = OpenLyricsSong(
            properties = OpenLyricsSong.Properties(
                titles = listOf(OpenLyricsSong.Title("Song 1")),
            ),
            lyrics = listOf(
                OpenLyricsSong.Verse(
                    name = "v1",
                    lines = listOf(OpenLyricsSong.Lines("Line 1\nLine 2")),
                )
            )
        )

        private val sampleOpenLyricsSongWithSongbook = OpenLyricsSong(
            properties = OpenLyricsSong.Properties(
                titles = listOf(OpenLyricsSong.Title("Song 1")),
                songbooks = listOf(OpenLyricsSong.Songbook(name = "songbook1", entry = "1"))
            ),
            lyrics = listOf(
                OpenLyricsSong.Verse(
                    name = "v1",
                    lines = listOf(OpenLyricsSong.Lines("Line 1\nLine 2")),
                )
            )
        )
    }
}
