package com.techbeloved.hymnbook.shared.openlyrics

import com.techbeloved.hymnbook.AuthorSongs
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.GetSongByTitleAndSongbook
import com.techbeloved.hymnbook.SongbookSongs
import com.techbeloved.hymnbook.TopicSongs
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import com.techbeloved.hymnbook.shared.model.Lyric
import com.techbeloved.hymnbook.shared.model.ext.OpenLyricsSong
import com.techbeloved.hymnbook.shared.model.ext.toLyric
import com.techbeloved.hymnbook.shared.time.DefaultInstantProvider
import com.techbeloved.hymnbook.shared.time.InstantProvider
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

internal class SaveOpenLyricsUseCase(
    private val database: Database = Injector.database,
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
    private val instantProvider: InstantProvider = DefaultInstantProvider(),
) {
    suspend operator fun invoke(song: OpenLyricsSong) = withContext(dispatchersProvider.io()) {
        val lyrics = song.lyrics.map { it.toLyric() }
        val created = instantProvider.get()
        database.transaction {
            // First check if song exists already, then we just update. Else we insert it.
            val songTitle = song.properties.titles.first().value
            val existingSong = database.songEntityQueries.getSongByTitleAndSongbook(
                title = songTitle,
                songbook = song.properties.songbooks?.firstOrNull()?.name,
            ).executeAsOneOrNull()
            val songId = insertSong(
                existingSong = existingSong,
                songTitle = songTitle,
                song = song,
                lyrics = lyrics,
                created = created,
            )

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
                database.authorEntityQueries.insert(author.value, null, null, null)
                AuthorSongs(author.value, songId, author.type, author.comment)
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

    private fun insertSong(
        existingSong: GetSongByTitleAndSongbook?,
        songTitle: String,
        song: OpenLyricsSong,
        lyrics: List<Lyric>,
        created: Instant,
    ): Long {
        if (existingSong != null) {
            database.songEntityQueries.update(
                title = songTitle,
                alternate_title = song.properties.titles.getOrNull(1)?.value,
                lyrics = lyrics,
                verse_order = song.properties.verseOrder,
                comments = song.properties.comments?.joinToString(),
                copyright = song.properties.copyright,
                search_title = song.properties.titles.joinToString(separator = " ") { it.value },
                search_lyrics = lyrics.joinToString(separator = " ") { it.content },
                modified = created,
                id = existingSong.id,
            )
        } else {
            database.songEntityQueries.insert(
                title = songTitle,
                alternate_title = song.properties.titles.getOrNull(1)?.value,
                lyrics = lyrics,
                verse_order = song.properties.verseOrder,
                comments = song.properties.comments?.joinToString(),
                copyright = song.properties.copyright,
                search_title = song.properties.titles.joinToString(separator = " ") { it.value },
                search_lyrics = lyrics.joinToString(separator = " ") { it.content },
                created = created,
                modified = created,
                id = null,
            )
        }
        return existingSong?.id
            ?: database.songEntityQueries.lastInsertRowId().executeAsOne()
    }
}
