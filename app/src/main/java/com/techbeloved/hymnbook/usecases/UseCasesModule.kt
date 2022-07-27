package com.techbeloved.hymnbook.usecases

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface UseCasesModule {

    @Binds
    fun bindsHymnbookUseCases(hymnbookUseCasesImp: HymnbookUseCasesImp): HymnbookUseCases
}