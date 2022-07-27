package com.techbeloved.hymnbook.playlists

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface PlaylistModule {
    @Binds
    fun bindPlaylistRepo(playlistsRepoImp: PlaylistsRepoImp): PlaylistsRepo
}