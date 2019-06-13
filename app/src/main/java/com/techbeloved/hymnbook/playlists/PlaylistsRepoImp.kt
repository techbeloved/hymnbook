package com.techbeloved.hymnbook.playlists

import com.techbeloved.hymnbook.data.model.Favorite
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.Playlist
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import io.reactivex.Completable
import io.reactivex.Observable

class PlaylistsRepoImp(
        val hymnsDatabase: HymnsDatabase
) : PlaylistsRepo {
    override fun getPlaylists(): Observable<List<Playlist>> {
        return hymnsDatabase.playlistsDao().getAllPlaylists().toObservable()
    }

    override fun getHymnsInPlaylist(playlistId: Int): Observable<List<Hymn>> {
        return hymnsDatabase.playlistsDao().getHymnsInPlaylist(playlistId).toObservable()
    }

    override fun getPlaylistById(playlistId: Int): Observable<Playlist> {
        return hymnsDatabase.playlistsDao().getPlaylist(playlistId).toObservable()
    }

    override fun savePlaylist(playlist: Playlist): Completable {
        return hymnsDatabase.playlistsDao().savePlaylist(playlist)
    }

    override fun savePlaylist(playlistId: Int, title: String, description: String): Completable {
        return hymnsDatabase.playlistsDao().updatePlaylist(playlistId, title, description)
    }

    override fun saveFavorite(favorite: Favorite): Completable {
        return hymnsDatabase.playlistsDao().saveFavorite(favorite)
    }
}