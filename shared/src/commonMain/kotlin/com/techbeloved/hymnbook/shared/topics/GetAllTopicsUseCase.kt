package com.techbeloved.hymnbook.shared.topics

import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.TopicEntity
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class GetAllTopicsUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {
    suspend operator fun invoke(): List<TopicEntity> = withContext(dispatchersProvider.io()) {
        database.topicEntityQueries.getAll().executeAsList().map(::TopicEntity)
    }
}
