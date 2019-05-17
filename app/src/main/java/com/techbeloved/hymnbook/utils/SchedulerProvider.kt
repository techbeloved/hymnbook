package com.techbeloved.hymnbook.utils

import io.reactivex.Scheduler

interface SchedulerProvider {
    fun io(): Scheduler
    fun ui(): Scheduler
}