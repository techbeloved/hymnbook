package com.techbeloved.hymnbook

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * A [ViewModel] shared by the fragments in order to update the main activity with their states, mostly the toolbar title
 */
class HymnbookViewModel : ViewModel() {
    private val toolbarTitleLiveData = MutableLiveData<String>()

    private val mutableEventStream = MutableLiveData<Event>()

    fun updateToolbarTitle(title: String) {
        toolbarTitleLiveData.value = title
    }

    fun processInputs(vararg events: Event) {
        for (event in events) {
            mutableEventStream.value = event
        }
    }

    val toolbarTitle: LiveData<String>
        get() = toolbarTitleLiveData

    val mainEventStream: LiveData<Event>
        get() = mutableEventStream


}

/**
 * Holds all the events that can happen on the screen. It is used to broadcast them to the screens that need them
 */
sealed class Event {
    data class Search(val query: String): Event()
    data class Filter(val filterBy: Int): Event()
}