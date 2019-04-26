package com.techbeloved.hymnbook

import androidx.lifecycle.ViewModel

/**
 * A [ViewModel] shared by the fragments in order to update the main activity with their states, mostly the toolbar title
 */
class HymnbookViewModel : ViewModel()

/**
 * Holds all the events that can happen on the screen. It is used to broadcast them to the screens that need them
 */
sealed class Event {
}