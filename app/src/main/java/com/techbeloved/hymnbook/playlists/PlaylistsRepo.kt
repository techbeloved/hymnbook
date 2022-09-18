package com.techbeloved.hymnbook.playlists

import com.techbeloved.hymnbook.data.model.Favorite
import com.techbeloved.hymnbook.data.model.HymnNumber
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.model.Playlist
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface PlaylistsRepo {
    fun getPlaylists(): Observable<List<Playlist>>

    fun getHymnsInPlaylist(playlistId: Int, sortBy: Int): Observable<List<HymnTitle>>

    fun getPlaylistById(playlistId: Int): Observable<Playlist>

    /**
     * Saves a new playlist and  returns the id
     */
    fun savePlaylist(playlist: Playlist): Single<Int>

    fun savePlaylist(playlistId: Int, title: String, description: String): Completable

    fun saveFavorite(favorite: Favorite): Completable

    fun deletePlaylistById(playlistId: Int): Completable

    fun loadHymnIndicesInPlaylist(playlistId: Int, sortBy: Int): Observable<List<HymnNumber>>
}