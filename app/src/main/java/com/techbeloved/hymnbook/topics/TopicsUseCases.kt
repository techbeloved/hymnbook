package com.techbeloved.hymnbook.topics

import io.reactivex.Observable

/**
 * Use to query and load topics from the repository and elsewhere
 */
interface TopicsUseCases {
    /**
     * Loads all available topics from repository
     */
    fun topics(): Observable<List<TopicItem>>
}
