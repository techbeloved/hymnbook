package com.techbeloved.hymnbook.di

import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.techbeloved.hymnbook.inappupdates.AppUpdatesModule
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn


@TestInstallIn(components = [SingletonComponent::class], replaces = [AppUpdatesModule::class])
class TestAppUpdatesModule {

    @Provides
    fun providesAppUpdatesManager(@ApplicationContext context: Context): AppUpdateManager =
        FakeAppUpdateManager(context)
}
