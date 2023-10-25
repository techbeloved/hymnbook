package com.techbeloved.hymnbook.usecases

import io.reactivex.Observable

/**
 * App wide use cases such as scheduling of download of midi archive, synchronisations, etc
 */
interface HymnbookUseCases {

    /**
     * Checks if app app just started for the first time
     */
    fun appFirstStart(): Observable<Boolean>

    /**
     * Updates the app status first start flag. This should be called in the very first onDestroy of activity
     */
    fun updateAppFirstStart(firstStart: Boolean)
}