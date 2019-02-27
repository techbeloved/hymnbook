package com.techbeloved.hymnbook.di

import android.app.Application
import com.techbeloved.hymnbook.HymnbookApp
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.data.repo.HymnsRepositoryImp

object Injection {
    fun provideAppContext(): Application {
        return HymnbookApp.instance
    }

    fun provideRepository(): HymnsRepository {
        return HymnsRepositoryImp.getInstance(HymnbookApp.database)
    }
}