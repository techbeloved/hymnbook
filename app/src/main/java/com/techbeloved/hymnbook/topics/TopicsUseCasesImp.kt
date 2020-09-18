package com.techbeloved.hymnbook.topics

import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.utils.SchedulerProvider
import io.reactivex.Observable

class TopicsUseCasesImp(private val hymnsRepository: HymnsRepository,
                        private val schedulerProvider: SchedulerProvider)
    : TopicsUseCases {

    override fun topics(): Observable<List<TopicItem>> {
        return hymnsRepository.loadAllTopics()
                .map { topics -> topics.map { topic -> TopicItem(topic.id, topic.topic) } }
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
    }

}