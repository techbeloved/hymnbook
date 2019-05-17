package com.techbeloved.hymnbook.sheetmusic

import com.techbeloved.hymnbook.data.download.Downloader
import com.techbeloved.hymnbook.data.model.*
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.hymnlisting.TitleItem
import com.techbeloved.hymnbook.utils.SchedulerProvider
import io.reactivex.FlowableTransformer
import io.reactivex.Observable

class HymnsUseCasesImp(private val hymnsRepository: HymnsRepository,
                       private val onlineRepo: OnlineRepo,
                       private val schedulerProvider: SchedulerProvider,
                       private val downloader: Downloader) : HymnUseCases {

    override fun hymnSheetMusicDetail(hymnId: Int): Observable<SheetMusicState> {
        return hymnsRepository.getHymnById(hymnId)
                .compose(resolveSheetMusicState())
                .observeOn(schedulerProvider.ui())
                .toObservable()
    }


    override fun hymnSheetMusicTitles(sortBy: Int): Observable<List<TitleItem>> {
        return onlineRepo.hymnIds(sortBy)
                .flatMap { ids -> hymnsRepository.loadHymnTitlesForIndices(ids) }
                .map { titles -> titles.map { it.toTitleItem() } }
                .subscribeOn(schedulerProvider.io())
    }

    override fun hymnSheetMusicIndices(sortBy: Int): Observable<List<Int>> {
        return onlineRepo.hymnIds(sortBy)
    }

    override fun downloadSheetMusic(hymnId: Int) {
        downloader.enqueueDownloadForHymn(hymnId)
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
