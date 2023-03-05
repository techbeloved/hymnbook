package com.techbeloved.hymnbook

import androidx.lifecycle.ViewModel
import com.techbeloved.hymnbook.usecases.HymnbookUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * A [ViewModel] shared by the fragments in order to update the main activity with their states, mostly the toolbar title
 */
@HiltViewModel
class HymnbookViewModel @Inject constructor(
    private val useCases: HymnbookUseCases,
) : ViewModel() {

    private val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        if (!disposables.isDisposed) disposables.dispose()
    }

    fun updateAppFirstStart(firstStart: Boolean) {
        useCases.updateAppFirstStart(firstStart)
    }
}
