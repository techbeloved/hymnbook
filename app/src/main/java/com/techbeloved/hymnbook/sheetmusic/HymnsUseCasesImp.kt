package com.techbeloved.hymnbook.sheetmusic

import com.techbeloved.hymnbook.data.download.Downloader
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.utils.SchedulerProvider
import io.reactivex.FlowableTransformer
import io.reactivex.Observable
import javax.inject.Inject

class HymnsUseCasesImp @Inject constructor(
    private val hymnsRepository: HymnsRepository,
    private val schedulerProvider: SchedulerProvider,
    private val downloader: Downloader,
) : HymnUseCases {

    override fun hymnSheetMusicDetail(hymnId: Int): Observable<SheetMusicState> {

        return hymnsRepository.getHymnById(hymnId)
            .compose(resolveSheetMusicState()).toObservable()
            .observeOn(schedulerProvider.ui())

    }

    override fun downloadSheetMusic(hymnId: Int) {
        downloader.enqueueDownloadForHymn(hymnId)
    }

    private fun resolveSheetMusicState(): FlowableTransformer<Hymn, SheetMusicState> =
        FlowableTransformer { upstream ->
            upstream.map { hymn ->
                val sheetMusicState: SheetMusicState = hymn.sheetMusic?.let { sheetMusic ->
                    if (sheetMusic.localUri != null) {
                        SheetMusicState.Ready(hymn.num, hymn.title, sheetMusic.localUri)
                    } else {
                        SheetMusicState.NotDownloaded(
                            hymn.num,
                            hymn.title,
                            sheetMusic.remoteUri
                        )
                    }
                } ?: SheetMusicState.NotDownloaded(hymn.num, hymn.title, null)
                sheetMusicState
            }
        }

}
