package com.techbeloved.hymnbook.shared.jsonimport

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.SongbookSongs
import com.techbeloved.hymnbook.TopicSongs
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.Lyric
import com.techbeloved.hymnbook.shared.model.jsonimport.JsonSongLyric
import com.techbeloved.hymnbook.shared.model.jsonimport.JsonSongbook
import com.techbeloved.hymnbook.shared.model.jsonimport.JsonSongbookMetadata
import com.techbeloved.hymnbook.shared.time.InstantProvider
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject

internal class SaveJsonSongUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
    private val instantProvider: InstantProvider,
) {
    suspend operator fun invoke(songbook: JsonSongbook) = withContext(dispatchersProvider.io()) {
        val created = instantProvider.get()
        database.transaction {
            database.songbookEntityQueries.insert(
                name = songbook.metadata.title,
                publisher = songbook.metadata.publisher,
            )
            for (topic in songbook.topics) {
                database.topicEntityQueries.insert(topic.topic)
            }
            for (song in songbook.songs) {

                val songId = insertOrUpdate(
                    song = song,
                    created = created,
                    metadata = songbook.metadata,
                )

                // SongbookSong
                val songbookSong = SongbookSongs(
                    songbook = songbook.metadata.title,
                    song_id = songId,
                    entry = song.number.toString(),
                )
                database.songbookSongsQueries.insert(
                    songbookSong.songbook,
                    songbookSong.song_id,
                    songbookSong.entry
                )

                // Topics
                val topicSong = TopicSongs(
                    topic = songbook.topics.find { it.id == song.topicId }?.topic,
                    song_id = songId,
                )

                database.topicSongsQueries.insert(
                    topic = topicSong.topic,
                    song_id = topicSong.song_id,
                )
                // Authors
                // Figure out how to deal with authors. Currently we only have credits, music by, and lyrics by.
                // Which are more of comments or history.
            }
        }
    }

    private fun insertOrUpdate(
        song: JsonSongLyric,
        created: Instant,
        metadata: JsonSongbookMetadata,
    ): Long {
        val existingSong = database.songEntityQueries.getSongByTitleAndSongbook(
            title = song.title,
            songbook = metadata.title,
        ).executeAsOneOrNull()
        val lyrics = lyricsFromJson(song)
        if (existingSong == null) {
            database.songEntityQueries.insert(
                title = song.title,
                alternate_title = null,
                lyrics = lyrics,
                verse_order = null,
                comments = null,
                copyright = null,
                search_title = song.title,
                search_lyrics = lyrics.joinToString(separator = " ") { it.content },
                search_songbook = "${song.number}",
                created = created,
                modified = created,
                id = null,
            )
        } else {
            database.songEntityQueries.update(
                title = song.title,
                alternate_title = null,
                lyrics = lyrics,
                verse_order = null,
                comments = null,
                copyright = null,
                search_title = song.title,
                search_lyrics = lyrics.joinToString(separator = " ") { it.content },
                search_songbook = "${song.number}",
                modified = created,
                id = existingSong.id,
            )
        }
        return existingSong?.id
            ?: database.songEntityQueries.lastInsertRowId().executeAsOne()
    }

    private fun lyricsFromJson(song: JsonSongLyric): List<Lyric> = buildList {
        if (!song.chorus.isNullOrBlank()) {
            add(
                Lyric(
                    type = Lyric.Type.Chorus,
                    label = "c1",
                    content = song.chorus,
                )
            )
        }
        val lyrics = song.verses.mapIndexed { index, verse ->
            Lyric(
                type = Lyric.Type.Verse,
                label = "v${index + 1}",
                content = verse,
            )
        }
        addAll(lyrics)
    }
}
