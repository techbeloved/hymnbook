package com.techbeloved.hymnbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * A [ViewModel] shared by the fragments in order to update the main activity with their states, mostly the toolbar title
 */
class HymnbookViewModel : ViewModel() {
    private val toolbarTitleLiveData = MutableLiveData<String>()

    fun updateToolbarTitle(title: String) {
        toolbarTitleLiveData.value = title
    }

    val toolbarTitle: LiveData<String>
        get() = toolbarTitleLiveData
}