package com.techbeloved.hymnbook.utils

import io.reactivex.subjects.PublishSubject

object RxBus {
    private val eventPublisher: PublishSubject<Any> = PublishSubject.create()

    fun publish(event: Any) = eventPublisher.onNext(event)

    fun <T> listen(eventType: Class<T>) = eventPublisher.ofType(eventType).hide()
}