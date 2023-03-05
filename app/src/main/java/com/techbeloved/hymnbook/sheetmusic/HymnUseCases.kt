package com.techbeloved.hymnbook.sheetmusic

import io.reactivex.Observable

/**
 * Handles some of the sheet music hymn listing and display use cases such as getting hymns from online
 *  source, that is hymns with sheet music. Downloading sheet music
 */
interface HymnUseCases {

    /**
     * Loads the sheet music hymn detail from local repo
     */
    fun hymnSheetMusicDetail(hymnId: Int): Observable<SheetMusicState>

    /**
     * Downloads sheet music for a hymn given the id
     */
    fun downloadSheetMusic(hymnId: Int)
}
