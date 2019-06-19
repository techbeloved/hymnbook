package com.techbeloved.hymnbook.sheetmusic

import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import com.techbeloved.hymnbook.data.download.Downloader
import com.techbeloved.hymnbook.data.model.*
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.hymnlisting.TitleItem
import com.techbeloved.hymnbook.utils.SchedulerProvider
import io.reactivex.FlowableTransformer
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class HymnsUseCasesImp(private val hymnsRepository: HymnsRepository,
                       private val onlineRepo: OnlineRepo,
                       private val schedulerProvider: SchedulerProvider,
                       private val downloader: Downloader,
                       private val sharedPreferences: SharedPreferencesRepo) : HymnUseCases {

    override fun hymnSheetMusicDetail(hymnId: Int): Observable<SheetMusicState> {
        // We want to get the latest night mode and push it to the screen since our pdf viewer will
        // have to set it's own night mode apart from system
        return Observable.combineLatest(
                hymnsRepository.getHymnById(hymnId)
                        .compose(resolveSheetMusicState()).toObservable(),
                sharedPreferences.isNightModeActive()
                        .subscribeOn(schedulerProvider.io()),

                BiFunction<SheetMusicState, Boolean, SheetMusicState> { state, nightMode ->
                    when (state) {
                        is SheetMusicState.Ready -> state.copy(darkMode = nightMode)
                        else -> state
                    }
                })

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
