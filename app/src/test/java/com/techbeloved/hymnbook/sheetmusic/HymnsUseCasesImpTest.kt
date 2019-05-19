package com.techbeloved.hymnbook.sheetmusic

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import com.techbeloved.hymnbook.data.download.Downloader
import com.techbeloved.hymnbook.data.model.DOWNLOAD_IN_PROGRESS
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.READY
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.data.repo.OnlineHymn
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.utils.SchedulerProvider
import com.techbeloved.hymnbook.utils.schedulers.ImmediateSchedulerProvider
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class HymnsUseCasesImpTest {

    private lateinit var subject: HymnsUseCasesImp


    @Mock
    private lateinit var hymnsRepoMock: HymnsRepository
    @Mock
    private lateinit var onlineRepoMock: OnlineRepo
    private lateinit var schedulerProvider: SchedulerProvider
    @Mock
    private lateinit var sharePrefsRepoMock: SharedPreferencesRepo
    @Mock
    private lateinit var downloaderMock: Downloader

    @Before
    fun setUp() {
        schedulerProvider = ImmediateSchedulerProvider()
        subject = HymnsUseCasesImp(
                hymnsRepoMock,
                onlineRepoMock,
                schedulerProvider,
                downloaderMock,
                sharePrefsRepoMock
        )


    }

    @Test
    fun shouldDownloadUpdatedSheetMusic_yes_onlineSheetMusicUrlDiffersFromLocallySavedUrl() {
        // Setup
        val hymn = Hymn("id", 1, "title", emptyList(), "first")
                .apply { sheetMusic = Hymn.SheetMusic(READY, 100, "remoteUrl", "localUrl") }
        val onlineHymn = OnlineHymn(1, "title", "remoteUrlLatest")

        whenever(hymnsRepoMock.getHymnById(any())).thenReturn(Flowable.just(hymn))
        whenever(onlineRepoMock.getHymnById(any())).thenReturn(Observable.just(onlineHymn))

        val testObserver = TestObserver<Boolean>()

        // Execute
        subject.shouldDownloadUpdatedSheetMusic(1).subscribe(testObserver)

        // Verify
        testObserver.assertSubscribed()
        testObserver.assertNoErrors()
        testObserver.assertValue(true)

    }

    @Test
    fun shouldDownloadUpdatedSheetMusic_no_onlineSheetMusicUrlSameWithLocallySavedUrl() {
        // Setup
        val hymn = Hymn("id", 1, "title", emptyList(), "first")
                .apply { sheetMusic = Hymn.SheetMusic(READY, 100, "remoteUrl", "localUrl") }
        val onlineHymn = OnlineHymn(1, "title", "remoteUrl")

        whenever(hymnsRepoMock.getHymnById(any())).thenReturn(Flowable.just(hymn))
        whenever(onlineRepoMock.getHymnById(any())).thenReturn(Observable.just(onlineHymn))

        val testObserver = TestObserver<Boolean>()

        // Execute
        subject.shouldDownloadUpdatedSheetMusic(1).subscribe(testObserver)

        // Verify
        testObserver.assertSubscribed()
        testObserver.assertNoErrors()
        testObserver.assertValue(false)
    }

    @Test
    fun shouldDownloadUpdatedSheetMusic_no_downloadStatusNotReady() {
        // Setup
        val hymn = Hymn(
                "id",
                1,
                "title", emptyList(),
                "first")
                .apply {
                    sheetMusic = Hymn.SheetMusic(
                            DOWNLOAD_IN_PROGRESS,
                            100,
                            "remoteUrl",
                            "localUrl")
                }

        val onlineHymn = OnlineHymn(
                1,
                "title",
                "remoteUrl")

        whenever(hymnsRepoMock.getHymnById(any())).thenReturn(Flowable.just(hymn))
        whenever(onlineRepoMock.getHymnById(any())).thenReturn(Observable.just(onlineHymn))

        val testObserver = TestObserver<Boolean>()

        // Execute
        subject.shouldDownloadUpdatedSheetMusic(1).subscribe(testObserver)

        // Verify
        testObserver.assertSubscribed()
        testObserver.assertNoErrors()
        testObserver.assertValue(false)
    }
}