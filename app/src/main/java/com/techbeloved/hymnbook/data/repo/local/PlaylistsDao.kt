package com.techbeloved.hymnbook.data.repo.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.techbeloved.hymnbook.data.model.Favorite
import com.techbeloved.hymnbook.data.model.HymnNumber
import com.techbeloved.hymnbook.data.model.HymnTitle
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

    // The following two queries does the same thing except ordering, one uses INNER JOIN while the other uses only WHERE clauses
    @Query("SELECT t.*,  f.playlistId FROM playlists AS p INNER JOIN favorites AS f ON p.id=f.playlistId INNER JOIN hymn_titles AS t ON f.hymnId=t.num WHERE f.playlistId =:playlistId ORDER BY t.num ASC")
    fun getHymnsInPlaylist(playlistId: Int): Flowable<List<HymnTitle>>

    @Query("SELECT t.*, playlistId FROM playlists AS p, favorites AS f, hymn_titles AS t WHERE p.id=f.playlistId  AND f.hymnId=t.num AND f.playlistId =:playlistId ORDER BY t.title ASC")
    fun getHymnsInPlaylistSortByTitle(playlistId: Int): Flowable<List<HymnTitle>>

    @Query("SELECT t.num AS number, CASE WHEN (localUri IS NULL) THEN 0 ELSE 1 END hasSheetMusic FROM playlists AS p, hymns as h, favorites AS f, hymn_titles AS t WHERE p.id=f.playlistId  AND f.hymnId=t.num AND h.num=f.hymnId AND f.playlistId =:playlistId ORDER BY t.title ASC")
    fun getHymnIndicesInPlaylistByTitle(playlistId: Int): Flowable<List<HymnNumber>>

    @Query("SELECT t.num AS number, CASE WHEN (localUri IS NULL) THEN 0 ELSE 1 END hasSheetMusic FROM hymns AS h, playlists AS p, favorites AS f, hymn_titles AS t WHERE p.id=f.playlistId  AND f.hymnId=t.num AND h.num=f.hymnId AND f.playlistId =:playlistId ORDER BY t.num ASC")
    fun getHymnIndicesInPlaylist(playlistId: Int): Flowable<List<HymnNumber>>

    @Insert
    fun savePlaylist(playlist: Playlist): Completable

    @Query("UPDATE playlists SET title =:title, description =:description, updated =:updated WHERE id =:playlistId")
    fun updatePlaylist(
        playlistId: Int,
        title: String,
        description: String,
        updated: Date = Date(),
    ): Completable

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