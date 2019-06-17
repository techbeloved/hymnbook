package com.techbeloved.hymnbook.playlists

import com.techbeloved.hymnbook.data.model.Favorite
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.model.Playlist
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.hymndetail.BY_TITLE
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class PlaylistsRepoImp(
        val hymnsDatabase: HymnsDatabase
) : PlaylistsRepo {
    override fun getPlaylists(): Observable<List<Playlist>> {
        return hymnsDatabase.playlistsDao().getAllPlaylists().toObservable()
    }

    override fun getHymnsInPlaylist(playlistId: Int, sortBy: Int): Observable<List<HymnTitle>> {
        return when (sortBy) {
            BY_TITLE -> hymnsDatabase.playlistsDao().getHymnsInPlaylistSortByTitle(playlistId).toObservable()
            else -> hymnsDatabase.playlistsDao().getHymnsInPlaylist(playlistId).toObservable()
        }
    }

    override fun getPlaylistById(playlistId: Int): Observable<Playlist> {
        return hymnsDatabase.playlistsDao().getPlaylist(playlistId).toObservable()
    }

    override fun savePlaylist(playlist: Playlist): Single<Int> {
        return hymnsDatabase.playlistsDao().savePlaylist(playlist)
                .andThen(hymnsDatabase.playlistsDao()
                        .getPlaylistByTitle(playlist.title)
                        .map { it.id }
                        .firstOrError())
    }

    override fun savePlaylist(playlistId: Int, title: String, description: String): Completable {
        return hymnsDatabase.playlistsDao().updatePlaylist(playlistId, title, description)
    }

    override fun saveFavorite(favorite: Favorite): Completable {
        return hymnsDatabase.playlistsDao().saveFavorite(favorite)
    }

    override fun deletePlaylistById(playlistId: Int): Completable {
        return hymnsDatabase.playlistsDao().deletePlaylistById(playlistId)
    }
}