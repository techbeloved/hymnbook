package com.techbeloved.hymnbook.inappupdates

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppUpdatesModule {

    @Provides
    fun providesInAppUpdateManager(@ApplicationContext context: Context): AppUpdateManager =
        AppUpdateManagerFactory.create(context)
}
