package com.techbeloved.hymnbook.data

import com.techbeloved.hymnbook.data.model.CatalogDownloadStatus
import io.reactivex.Observable

interface MediaAssetUseCases {
    /**
     * Takes care of checking all the necessary statuses of the music catalog and taking necessary actions
     */
    fun processMusicCatalog(): Observable<CatalogDownloadStatus>
}
