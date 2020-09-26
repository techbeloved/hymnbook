package com.techbeloved.hymnbook.sheetmusic

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
interface HymnUseCaseModule {

    @Binds
    fun bindsHymnsUseCases(hymnsUseCasesImp: HymnsUseCasesImp): HymnUseCases
}