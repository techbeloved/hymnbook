package com.techbeloved.hymnbook.data.download

import android.content.Context
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
interface DownloadModule {

    @Binds
    fun downloader(downloaderImp: DownloaderImp): Downloader

    companion object {
        @Provides
        @Named("CacheDir")
        fun provideCacheDir(@ApplicationContext context: Context): File = context.getExternalFilesDir(null)
                ?: context.filesDir

        @Provides
        fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
    }
}