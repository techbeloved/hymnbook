package com.techbeloved.hymnbook.shared.repository

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.deleteAll
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.model.SongTitle
import com.techbeloved.hymnbook.shared.model.ext.OpenLyricsSong
import com.techbeloved.hymnbook.shared.testDatabaseDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.Instant
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SongRepositoryTest {

    private val scope = TestScope()
    private lateinit var repository: SongRepository
    private lateinit var database: Database

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(scope.testScheduler))
        database = Injector.getDatabase(testDatabaseDriver())
        repository = SongRepository(
            database,
            instantProvider = { Instant.parse(isoString = "2023-01-01T00:00:00Z") },
        )
    }

    @AfterTest
    fun tearDown() {
        database.deleteAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `Given an empty database_When a single song is inserted_Then allTitles returns the single song inserted`() =
        runTest {
            assertEquals(expected = emptyList(), actual = repository.allTitles())
            repository.saveOpenLyrics(sampleOpenLyricsSong)
            assertEquals(
                expected = listOf(
                    SongTitle(
                        id = 1L,
                        title = "Song 1",
                        alternateTitle = null,
                        songbook = null,
                        songbookEntry = null
                    )
                ), actual = repository.allTitles()
            )
        }

    @Test
    fun `Given an empty database_When a single song with songbook is inserted_Then allTitles returns the single song`() =
        runTest {
            assertEquals(expected = emptyList(), actual = repository.allTitles())
            repository.saveOpenLyrics(sampleOpenLyricsSongWithSongbook)
            assertEquals(
                expected = listOf(
                    SongTitle(
                        id = 1L,
                        title = "Song 1",
                        alternateTitle = null,
                        songbook = "songbook1",
                        songbookEntry = "1"
                    )
                ), actual = repository.allTitles()
            )
        }

    @Test
    fun `Given an empty database_When a two songs are inserted_Then allTitles returns the all songs`() =
        runTest {
            assertEquals(expected = emptyList(), actual = repository.allTitles())
            repository.saveOpenLyrics(sampleOpenLyricsSongWithSongbook)
            repository.saveOpenLyrics(sampleOpenLyricsSong)
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
                ), actual = repository.allTitles()
            )
        }


    companion object {
        private val sampleOpenLyricsSong = OpenLyricsSong(
            metadata = OpenLyricsSong.Metadata(),
            properties = OpenLyricsSong.Properties(
                titles = listOf(OpenLyricsSong.Title("Song 1")),
            ),
            lyrics = listOf(
                OpenLyricsSong.Verse(
                    name = "v1",
                    lines = listOf(OpenLyricsSong.Line("Line 1\nLine 2")),
                )
            )
        )

        private val sampleOpenLyricsSongWithSongbook = OpenLyricsSong(
            metadata = OpenLyricsSong.Metadata(),
            properties = OpenLyricsSong.Properties(
                titles = listOf(OpenLyricsSong.Title("Song 1")),
                songbooks = listOf(OpenLyricsSong.Songbook(name = "songbook1", entry = "1"))
            ),
            lyrics = listOf(
                OpenLyricsSong.Verse(
                    name = "v1",
                    lines = listOf(OpenLyricsSong.Line("Line 1\nLine 2")),
                )
            )
        )
    }
}
