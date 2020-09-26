package com.techbeloved.hymnbook.playlists

import com.techbeloved.hymnbook.data.model.Favorite
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.model.Playlist
import com.techbeloved.hymnbook.data.repo.local.PlaylistsDao
import com.techbeloved.hymnbook.hymndetail.BY_TITLE
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class PlaylistsRepoImp @Inject constructor(private val playlistsDao: PlaylistsDao) : PlaylistsRepo {
    override fun getPlaylists(): Observable<List<Playlist>> {
        return playlistsDao.getAllPlaylists().toObservable()
    }

    override fun getHymnsInPlaylist(playlistId: Int, sortBy: Int): Observable<List<HymnTitle>> {
        return when (sortBy) {
            BY_TITLE -> playlistsDao.getHymnsInPlaylistSortByTitle(playlistId).toObservable()
            else -> playlistsDao.getHymnsInPlaylist(playlistId).toObservable()
        }
    }

    override fun getPlaylistById(playlistId: Int): Observable<Playlist> {
        return playlistsDao.getPlaylist(playlistId).toObservable()
    }

    override fun savePlaylist(playlist: Playlist): Single<Int> {
        return playlistsDao.savePlaylist(playlist)
                .andThen(playlistsDao
                        .getPlaylistByTitle(playlist.title)
                        .map { it.id }
                        .firstOrError())
    }

    override fun savePlaylist(playlistId: Int, title: String, description: String): Completable {
        return playlistsDao.updatePlaylist(playlistId, title, description)
    }

    override fun saveFavorite(favorite: Favorite): Completable {
        return playlistsDao.saveFavorite(favorite)
    }

    override fun deletePlaylistById(playlistId: Int): Completable {
        return playlistsDao.deletePlaylistById(playlistId)
    }

    override fun loadHymnIndicesInPlaylist(playlistId: Int, sortBy: Int): Observable<List<Int>> {
        return when (sortBy) {
            BY_TITLE -> playlistsDao.getHymnIndicesInPlaylistByTitle(playlistId).toObservable()
            else -> playlistsDao.getHymnIndicesInPlaylist(playlistId).toObservable()
        }

    }
}