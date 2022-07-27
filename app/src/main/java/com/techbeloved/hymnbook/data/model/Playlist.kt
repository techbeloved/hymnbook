package com.techbeloved.hymnbook.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "playlists", indices = [Index("title", unique = true)])
data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String?,
    val created: Date,
    val updated: Date = Date()
)

@Entity(
    tableName = "favorites",
    indices = [Index("hymnId", "playlistId", unique = true)],
    foreignKeys = [ForeignKey(
        entity = Playlist::class,
        parentColumns = ["id"],
        childColumns = ["playlistId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playlistId: Int,
    val hymnId: Int
)