package com.techbeloved.hymnbook.di

import android.content.Context
import androidx.work.WorkManager
import com.techbeloved.hymnbook.utils.SchedulerProvider
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {

    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager = WorkManager.getInstance(context)

    @Provides
    fun provideResources(@ApplicationContext context: Context) = context.resources

    @Provides
    @Reusable
    fun provideSchedulerProvider(): SchedulerProvider = object : SchedulerProvider {
        override fun io(): Scheduler = Schedulers.io()

        override fun ui(): Scheduler = AndroidSchedulers.mainThread()
    }
}