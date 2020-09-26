package com.techbeloved.hymnbook.usecases

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
interface UseCasesModule {

    @Binds
    fun bindsHymnbookUseCases(hymnbookUseCasesImp: HymnbookUseCasesImp): HymnbookUseCases
}