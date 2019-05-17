package com.techbeloved.hymnbook.sheetmusic

import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.hymndetail.SortBy
import com.techbeloved.hymnbook.hymnlisting.TitleItem
import io.reactivex.Observable

/**
 * Handles some of the hymn listing and display use cases such as getting online hymns and local hymns and
 * checking when there is new update
 */
interface HymnUseCases {
    fun hymnSheetMusicTitles(@SortBy sortBy: Int = BY_NUMBER): Observable<List<TitleItem>>

    fun hymnSheetMusicIndices(@SortBy sortBy: Int): Observable<List<Int>>

    fun hymnSheetMusicDetail(hymnId: Int): Observable<SheetMusicState>

    fun downloadSheetMusic(hymnId: Int)
}
