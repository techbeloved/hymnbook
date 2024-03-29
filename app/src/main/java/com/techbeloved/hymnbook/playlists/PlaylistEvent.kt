package com.techbeloved.hymnbook.playlists

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Playlist event can be sent from different sections of the app, so we like to manage all event
 * centrally in one place. Using RxBus to communicate things
 */
sealed class PlaylistEvent {
    data class Create(val title: String, val description: String?, val created: Date) : PlaylistEvent()
    @Parcelize
    data class Update(val id: Int, val title: String, val description: String) : PlaylistEvent(), Parcelable
    data class Delete(val id: Int) : PlaylistEvent()
    data class SaveFavorite(val playlistId: Int, val hymnId: Int) : PlaylistEvent()
    data class DeleteFavorite(val playlistId: Int, val hymnId: Int) : PlaylistEvent()
}