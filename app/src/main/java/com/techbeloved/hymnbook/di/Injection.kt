package com.techbeloved.hymnbook.di

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.techbeloved.hymnbook.HymnbookApp
import com.techbeloved.hymnbook.data.download.Downloader
import com.techbeloved.hymnbook.data.download.DownloaderImp
import com.techbeloved.hymnbook.data.repo.FirebaseRepo
import com.techbeloved.hymnbook.data.repo.HymnsRepositoryImp
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.sheetmusic.HymnUseCases
import com.techbeloved.hymnbook.sheetmusic.HymnsUseCasesImp
import com.techbeloved.hymnbook.utils.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executors

const val WCCRM_HYMNS_COLLECTION = "wccrm"

object Injection {
    fun provideAppContext(): Application {
        return HymnbookApp.instance
    }

    fun provideRepository() = lazy {
        HymnsRepositoryImp.getInstance(HymnbookApp.database)
    }

    fun provideOnlineRepo() = lazy<OnlineRepo> {
        FirebaseRepo(Executors.newSingleThreadExecutor(), FirebaseFirestore.getInstance(), WCCRM_HYMNS_COLLECTION)
    }

    val provideHymnUsesCases: HymnUseCases by lazy {
        HymnsUseCasesImp(provideRepository().value,
                provideOnlineRepo().value,
                provideSchedulers,
                provideDownloader)
    }

    val provideDownloader: Downloader by lazy {
        val cacheDir = HymnbookApp.instance.externalCacheDir ?: HymnbookApp.instance.cacheDir
        DownloaderImp(FirebaseStorage.getInstance(),
                cacheDir,
                provideRepository().value,
                provideOnlineRepo().value,
                provideSchedulers,
                Executors.newSingleThreadExecutor())
    }
    val provideSchedulers: SchedulerProvider by lazy {
        object : SchedulerProvider {
            override fun io(): Scheduler = Schedulers.io()

            override fun ui(): Scheduler = AndroidSchedulers.mainThread()
        }
    }
}