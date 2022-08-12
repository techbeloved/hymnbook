package com.techbeloved.hymnbook.sheetmusic

import com.techbeloved.hymnbook.data.download.Downloader
import com.techbeloved.hymnbook.data.model.DOWNLOAD_FAILED
import com.techbeloved.hymnbook.data.model.DOWNLOAD_IN_PROGRESS
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.model.READY
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.hymnlisting.TitleItem
import com.techbeloved.hymnbook.utils.SchedulerProvider
import io.reactivex.FlowableTransformer
import io.reactivex.Observable
import javax.inject.Inject

class HymnsUseCasesImp @Inject constructor(
    private val hymnsRepository: HymnsRepository,
    private val onlineRepo: OnlineRepo,
    private val schedulerProvider: SchedulerProvider,
    private val downloader: Downloader
) : HymnUseCases {

    override fun hymnSheetMusicDetail(hymnId: Int): Observable<SheetMusicState> {

        return hymnsRepository.getHymnById(hymnId)
            .compose(resolveSheetMusicState()).toObservable()
            .observeOn(schedulerProvider.ui())

    }


    override fun hymnSheetMusicTitles(sortBy: Int): Observable<List<TitleItem>> {
        return onlineRepo.hymnIds(sortBy)
                .flatMap { ids -> hymnsRepository.loadHymnTitlesForIndices(ids, sortBy) }
                .map { titles -> titles.map { it.toTitleItem() } }
                .subscribeOn(schedulerProvider.io())
    }

    override fun hymnSheetMusicIndices(sortBy: Int): Observable<List<Int>> {
        return onlineRepo.hymnIds(sortBy)
    }

    override fun downloadSheetMusic(hymnId: Int) {
        downloader.enqueueDownloadForHymn(hymnId)
    }

    /**
     * Returns true if music has been downloaded previously but the remote uri has changed
     */
    override fun shouldDownloadUpdatedSheetMusic(hymnId: Int): Observable<Boolean> {
        return hymnsRepository.getHymnById(hymnId)
                .toObservable()
                .flatMap { hymn ->
                    onlineRepo.getHymnById(hymnId)
                            .map { onlineHymn ->
                                (hymn.sheetMusic?.downloadStatus == READY)
                                        && (hymn.sheetMusic?.remoteUri != onlineHymn.sheetMusicUrl)
                            }
                }.subscribeOn(schedulerProvider.io())
    }

    private fun resolveSheetMusicState(): FlowableTransformer<Hymn, SheetMusicState> = FlowableTransformer { upstream ->
        upstream.map { hymn ->
            val sheetMusicState: SheetMusicState = hymn.sheetMusic?.let { sheetMusic ->
                when (sheetMusic.downloadStatus) {
                    DOWNLOAD_IN_PROGRESS -> SheetMusicState.Downloading(hymn.num, hymn.title, sheetMusic.downloadProgress)
                    DOWNLOAD_FAILED -> SheetMusicState.DownloadFailed(hymn.num, hymn.title, sheetMusic.remoteUri)
                    READY -> SheetMusicState.Ready(hymn.num, hymn.title, sheetMusic.localUri)
                    else -> SheetMusicState.NotDownloaded(hymn.num, hymn.title, sheetMusic.remoteUri)
                }

            } ?: SheetMusicState.NotDownloaded(hymn.num, hymn.title, null)
            sheetMusicState
        }
    }

}


fun HymnTitle.toTitleItem(): TitleItem {
    return TitleItem(id, title)
}
