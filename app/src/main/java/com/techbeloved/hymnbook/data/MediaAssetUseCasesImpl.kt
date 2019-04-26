package com.techbeloved.hymnbook.data

import com.techbeloved.hymnbook.data.download.DownloadService
import com.techbeloved.hymnbook.data.download.DownloadStatus
import com.techbeloved.hymnbook.data.model.*
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import io.reactivex.Observable
import io.reactivex.ObservableTransformer
import io.reactivex.functions.Consumer

class MediaAssetUseCasesImpl(private val preferencesRepo: SharedPreferencesRepo,
                             private val fileManager: FileManager,
                             private val downloadService: DownloadService,
                             private val onlineRepo: OnlineRepo) : MediaAssetUseCases {

    override fun processMusicCatalog(): Observable<CatalogDownloadStatus> {
        // First Check that catalog has been downloaded and processed
        return preferencesRepo.hymnsCatalogDownloadStatus()
                .compose(processCatalogDownloadStatus())
                .distinctUntilChanged()
                .doOnNext(updateCatalogPreferencesStatus())
    }

    private fun updateCatalogPreferencesStatus(): Consumer<in CatalogDownloadStatus>? {
        return Consumer { catalogStatus ->
            when (catalogStatus) {
                CatalogDownloadStatus.Downloaded -> preferencesRepo.updateHymnsCatalogStatus(DOWNLOADED)
                is CatalogDownloadStatus.Failure -> preferencesRepo.updateHymnsCatalogStatus(DOWNLOAD_FAILED)
                is CatalogDownloadStatus.DownloadInProgress,
                is CatalogDownloadStatus.DownloadEnqueued,
                is CatalogDownloadStatus.Paused -> preferencesRepo.updateHymnsCatalogStatus(DOWNLOAD_IN_PROGRESS)
                is CatalogDownloadStatus.CatalogReady -> preferencesRepo.updateHymnsCatalogStatus(READY)
            }
        }
    }

    private fun processCatalogDownloadStatus(): ObservableTransformer<in Int, out CatalogDownloadStatus> {
        return ObservableTransformer { upstream ->
            upstream.distinctUntilChanged()
                    .flatMap { status ->
                        // Returns Catalog Ready signal immediately
                        when (status) {
                            READY -> Observable.just(CatalogDownloadStatus.CatalogReady)
                            DOWNLOADED, DOWNLOAD_IN_PROGRESS -> preferencesRepo.hymnsCatalogDownloadId()
                                    // Do not take invalid download id
                                    .skipWhile { it < 0 }
                                    .compose(getDownloadStatus())
                            // We want to start a new download
                            else -> onlineRepo.getLatestCatalogUrl()
                                    .compose(processNewDownloadRequest())
                        }
                        // Check the download status
                    }
        }
    }

    private fun processNewDownloadRequest(): ObservableTransformer<in String, out CatalogDownloadStatus> {
        return ObservableTransformer { upstream ->
            upstream.flatMap { catalogUrl ->
                downloadService.enqueueDownload(catalogUrl)
                        // Update the shared preference
                        .doOnNext { downloadId -> preferencesRepo.updateHymnsCatalogDownloadId(downloadId) }
                        .map { CatalogDownloadStatus.DownloadEnqueued(it) }
            }
        }
    }

    private fun getDownloadStatus(): ObservableTransformer<in Long, out CatalogDownloadStatus>? {
        return ObservableTransformer { upstream ->
            upstream.flatMap { downloadId ->
                downloadService.downloadStatus(downloadId)
                        .distinctUntilChanged() // To prevent some kind of infinite loop when shared preferences is updated on getting download status, triggering another download status check and so on
                        .map { downloadStatus ->
                            // Convert to appropriate catalog download status objects
                            when (downloadStatus) {
                                DownloadStatus.Success -> CatalogDownloadStatus.Downloaded
                                is DownloadStatus.Failure -> CatalogDownloadStatus.Failure(Throwable(downloadStatus.reason.toString()))
                                is DownloadStatus.Running -> CatalogDownloadStatus.DownloadInProgress(downloadStatus.progress)
                                DownloadStatus.Pending -> CatalogDownloadStatus.DownloadEnqueued(downloadId)
                                is DownloadStatus.Paused -> CatalogDownloadStatus.Paused(downloadStatus.reason.toString())
                            }
                        }
            }
        }
    }
}