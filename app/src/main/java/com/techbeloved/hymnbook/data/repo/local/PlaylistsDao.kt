package com.techbeloved.hymnbook.data.repo.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.techbeloved.hymnbook.data.model.Favorite
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.Playlist
import io.reactivex.Completable
import io.reactivex.Flowable
import java.util.*

@Dao
interface PlaylistsDao {
    @Query("SELECT * FROM playlists ORDER BY created ASC")
    fun getAllPlaylists(): Flowable<List<Playlist>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun getPlaylist(playlistId: Int): Flowable<Playlist>

    @Query("SELECT *, *, * FROM hymns AS h, favorites AS f, playlists AS p WHERE h.num=f.hymnId AND f.playlistId =:playlistId ORDER BY f.id ASC")
    fun getHymnsInPlaylist(playlistId: Int): Flowable<List<Hymn>>

    @Insert
    fun savePlaylist(playlist: Playlist): Completable

    @Query("UPDATE playlists SET title =:title, description =:description, updated =:updated WHERE id =:playlistId")
    fun updatePlaylist(playlistId: Int, title: String, description: String, updated: Date = Date()): Completable

    @Insert
    fun saveFavorite(favorite: Favorite): Completable

    @Insert
    fun saveFavorites(favorites: List<Favorite>): Completable

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    fun deletePlaylistById(playlistId: Int): Completable

    @Query("DELETE FROM playlists WHERE id IN (:playlistIds)")
    fun deleteAllPlaylistsById(playlistIds: List<Int>): Completable

    @Query("DELETE FROM playlists")
    fun deleteAllPlaylists(): Completable

    @Query("SELECT * FROM playlists WHERE title LIKE :title")
    fun getPlaylistByTitle(title: String): Flowable<Playlist>
}