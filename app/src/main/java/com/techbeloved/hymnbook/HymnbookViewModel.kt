package com.techbeloved.hymnbook

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.techbeloved.hymnbook.usecases.HymnbookUseCases
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

/**
 * A [ViewModel] shared by the fragments in order to update the main activity with their states, mostly the toolbar title
 */
class HymnbookViewModel @ViewModelInject constructor(private val useCases: HymnbookUseCases) : ViewModel() {

    private val disposables = CompositeDisposable()

    fun shouldDownloadHymnMidiArchive() {
        useCases.appFirstStart()
                .subscribe({ firstStart ->
                    if (firstStart) {
                        useCases.downloadLatestHymnMidiArchive()
                    }
                }, { Timber.w(it, "Error checking first start status") })
                .let { disposables.add(it) }
    }

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }

    fun updateAppFirstStart(firstStart: Boolean) {
        useCases.updateAppFirstStart(firstStart)
    }
}

/**
 * Holds all the events that can happen on the screen. It is used to broadcast them to the screens that need them
 */
sealed class Event {
}