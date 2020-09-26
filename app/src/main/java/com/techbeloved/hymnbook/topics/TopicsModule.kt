package com.techbeloved.hymnbook.topics

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent

@Module
@InstallIn(ApplicationComponent::class)
interface TopicsModule {

    @Binds
    fun bindTopicUseCases(topicsUseCasesImp: TopicsUseCasesImp): TopicsUseCases
}