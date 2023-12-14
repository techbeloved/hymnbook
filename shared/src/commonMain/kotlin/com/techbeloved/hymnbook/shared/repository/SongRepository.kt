package com.techbeloved.hymnbook.shared.repository

import com.techbeloved.hymnbook.AuthorSongs
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.SongbookSongs
import com.techbeloved.hymnbook.TopicSongs
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import com.techbeloved.hymnbook.shared.model.SongTitle
import com.techbeloved.hymnbook.shared.model.ext.OpenLyricsSong
import com.techbeloved.hymnbook.shared.model.ext.toLyric
import com.techbeloved.hymnbook.shared.time.DefaultInstantProvider
import com.techbeloved.hymnbook.shared.time.InstantProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.withContext

internal class SongRepository(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
    private val instantProvider: InstantProvider = DefaultInstantProvider(),
) {

    suspend fun saveOpenLyrics(song: OpenLyricsSong) = withContext(dispatchersProvider.io()) {
        val lyrics = song.lyrics.map { it.toLyric() }
        val created = instantProvider.get()
        database.transaction {

            database.songEntityQueries.insert(
                title = song.properties.titles.first().name,
                alternate_title = song.properties.titles.getOrNull(1)?.name,
                lyrics = lyrics,
                verse_order = song.properties.verseOrder,
                comments = song.properties.comments?.joinToString(),
                copyright = song.properties.copyright,
                search_title = song.properties.titles.joinToString(separator = " ") { it.name },
                search_lyrics = lyrics.joinToString(separator = " ") { it.content },
                created = created,
                modified = created,
            )
            val songId = database.songEntityQueries.lastInsertRowId().executeAsOne()

            // Songbook
            val songbookSongs = song.properties.songbooks?.map { songbook ->
                database.songbookEntityQueries.insert(songbook.name, null)
                SongbookSongs(songbook.name, songId, songbook.entry)
            }
            songbookSongs?.forEach { songbookSong ->
                database.songbookSongsQueries.insert(
                    songbookSong.songbook,
                    songbookSong.song_id,
                    songbookSong.entry
                )
            }

            // Topics
            val topicSongs = song.properties.themes?.map { theme ->
                database.topicEntityQueries.insert(theme.name)
                TopicSongs(theme.name, songId)
            }
            topicSongs?.forEach { topic ->
                database.topicSongsQueries.insert(topic.topic, topic.song_id)
            }

            // Authors
            val authorSongs = song.properties.authors?.map { author ->
                database.authorEntityQueries.insert(author.name, null, null, null)
                AuthorSongs(author.name, songId, author.type, author.comment)
            }
            authorSongs?.forEach { authorSong ->
                database.authorSongsQueries.insert(
                    authorSong.author,
                    authorSong.song_id,
                    authorSong.author_type,
                    authorSong.comment,
                )
            }
        }
    }

    suspend fun allTitles(): ImmutableList<SongTitle> = withContext(dispatchersProvider.io()) {
        database.songEntityQueries.getAllTitles(::SongTitle).executeAsList()
            .toImmutableList()
    }
}
