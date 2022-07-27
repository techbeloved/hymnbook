package com.techbeloved.hymnbook.topics

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface TopicsModule {

    @Binds
    fun bindTopicUseCases(topicsUseCasesImp: TopicsUseCasesImp): TopicsUseCases
}