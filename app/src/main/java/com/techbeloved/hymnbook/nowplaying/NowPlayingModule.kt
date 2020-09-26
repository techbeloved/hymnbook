package com.techbeloved.hymnbook.nowplaying

import android.content.ComponentName
import android.content.Context
import com.techbeloved.hymnbook.data.PlayerPreferences
import com.techbeloved.hymnbook.tunesplayback.TunesPlayerService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object NowPlayingModule {

    @Provides
    @Singleton
    fun provideMediaSessionConnection(@ApplicationContext context: Context, playerPreferences: PlayerPreferences): MediaSessionConnection = MediaSessionConnection.getInstance(context,
            ComponentName(context, TunesPlayerService::class.java),
            playerPreferences)

}