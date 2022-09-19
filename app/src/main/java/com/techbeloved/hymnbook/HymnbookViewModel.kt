package com.techbeloved.hymnbook

import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.techbeloved.hymnbook.usecases.HymnbookUseCases
import com.techbeloved.hymnbook.utils.workers.HymnSyncWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * A [ViewModel] shared by the fragments in order to update the main activity with their states, mostly the toolbar title
 */
@HiltViewModel
class HymnbookViewModel @Inject constructor(
    private val useCases: HymnbookUseCases,
    private val workManager: WorkManager
) : ViewModel() {

    private val disposables = CompositeDisposable()

    // Ensures the viewModel is initialized and necessary tasks are run
    fun onShown() = Unit

    init {
        synchronizeOnlineMusic()
    }

    private fun synchronizeOnlineMusic() {
        workManager.beginUniqueWork(HymnSyncWorker.TAG, ExistingWorkPolicy.KEEP, HymnSyncWorker.create())
            .enqueue()
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