package com.techbeloved.hymnbook.sheetmusic

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface HymnUseCaseModule {

    @Binds
    fun bindsHymnsUseCases(hymnsUseCasesImp: HymnsUseCasesImp): HymnUseCases
}