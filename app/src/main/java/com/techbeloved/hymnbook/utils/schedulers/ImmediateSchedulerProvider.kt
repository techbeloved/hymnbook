package com.techbeloved.hymnbook.utils.schedulers

import com.techbeloved.hymnbook.utils.SchedulerProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class ImmediateSchedulerProvider : SchedulerProvider {
    override fun io(): Scheduler = Schedulers.trampoline()

    override fun ui(): Scheduler = Schedulers.trampoline()
}