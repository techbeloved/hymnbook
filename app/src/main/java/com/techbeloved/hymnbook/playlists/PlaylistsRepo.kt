package com.techbeloved.hymnbook.playlists

import com.techbeloved.hymnbook.data.model.Favorite
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.Playlist
import io.reactivex.Completable
import io.reactivex.Observable

interface PlaylistsRepo {
    fun getPlaylists(): Observable<List<Playlist>>

    fun getHymnsInPlaylist(playlistId: Int): Observable<List<Hymn>>

    fun getPlaylistById(playlistId: Int): Observable<Playlist>

    fun savePlaylist(playlist: Playlist): Completable

    fun savePlaylist(playlistId: Int, title: String, description: String): Completable

    fun saveFavorite(favorite: Favorite): Completable
}