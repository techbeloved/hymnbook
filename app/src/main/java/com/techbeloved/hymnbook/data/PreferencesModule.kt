package com.techbeloved.hymnbook.data

import android.content.Context
import androidx.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.techbeloved.hymnbook.BuildConfig
import com.techbeloved.hymnbook.utils.DYNAMIC_LINK_DOMAIN
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface PreferencesModule {

    @Binds
    fun bindPlayerPreferences(playerPreferencesImp: PlayerPreferencesImp): PlayerPreferences

    @Binds
    fun bindSharedPrefRepo(sharedPreferencesRepoImp: SharedPreferencesRepoImp): SharedPreferencesRepo

    @Binds
    fun bindFileManager(fileManagerImp: FileManagerImp): FileManager

    companion object {
        @Provides
        @Singleton
        fun provideRxSharedPreferences(@ApplicationContext context: Context) = RxSharedPreferences.create(PreferenceManager
                .getDefaultSharedPreferences(context))

        @Provides
        @Named("DYNAMIC_LINK_DOMAIN")
        fun provideDynamicLinkDomain() = DYNAMIC_LINK_DOMAIN

        @Provides
        @Named("APP_ID")
        fun providesAppId() = BuildConfig.APPLICATION_ID

        @Provides
        fun provideDynamicLink(): FirebaseDynamicLinks = FirebaseDynamicLinks.getInstance()

    }
}