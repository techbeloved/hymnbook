package com.techbeloved.hymnbook.playlists

import com.techbeloved.hymnbook.data.model.Favorite
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.Playlist
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface PlaylistsRepo {
    fun getPlaylists(): Observable<List<Playlist>>

    fun getHymnsInPlaylist(playlistId: Int): Observable<List<Hymn>>

    fun getPlaylistById(playlistId: Int): Observable<Playlist>

    /**
     * Saves a new playlist and  returns the id
     */
    fun savePlaylist(playlist: Playlist): Single<Int>

    fun savePlaylist(playlistId: Int, title: String, description: String): Completable

    fun saveFavorite(favorite: Favorite): Completable
}