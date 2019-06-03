package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.data.model.*
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.di.SingletonHolder
import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.hymndetail.BY_TITLE
import com.techbeloved.hymnbook.hymndetail.SortBy
import io.reactivex.Flowable
import io.reactivex.Observable

class HymnsRepositoryImp (private val hymnDatabase: HymnsDatabase) : HymnsRepository {
    override fun loadHymnTitlesForIndices(indices: List<Int>): Observable<List<HymnTitle>> {
        return hymnDatabase.hymnDao().getHymnTitlesForIndices(indices).toObservable()
    }

    companion object: SingletonHolder<HymnsRepository, HymnsDatabase>(::HymnsRepositoryImp);

    override fun searchHymns(searchTerm: String): Flowable<List<SearchResult>> {
        return hymnDatabase.hymnDao().searchHymns("$searchTerm*")
    }

    override fun getHymnById(hymnNo: Int): Flowable<Hymn> {
        return hymnDatabase.hymnDao().getHymnByNumber(hymnNo)
    }

    override fun loadHymnIndices(sortBy: Int): Flowable<List<Int>> {
        return when (sortBy) {
            BY_TITLE -> hymnDatabase.hymnDao().getIndicesByTitle()
            BY_NUMBER -> hymnDatabase.hymnDao().getIndicesByNumber()
            else -> Flowable.error(Throwable("Unsupported sort type"))
        }
    }

    override fun getHymnDetailByNumber(hymnNo: Int): Flowable<HymnDetail> {
        return hymnDatabase.hymnDao().getHymnDetail(hymnNo)
    }

    override fun loadHymnTitles(@SortBy sortBy: Int): Flowable<List<HymnTitle>> {
        return when(sortBy) {
            BY_TITLE -> hymnDatabase.hymnDao().getAllHymnTitlesSortedByTitles()
            else -> hymnDatabase.hymnDao().getAllHymnTitles()
        }
    }

    override fun updateHymnDownloadProgress(hymnId: Int, progress: Int, downloadStatus: Int) {
        hymnDatabase.hymnDao().updateSheetMusicDownloadProgress(hymnId, downloadStatus, progress)
    }

    override fun updateHymnDownloadStatus(hymnId: Int,
                                          progress: Int,
                                          downloadStatus: Int,
                                          remoteUri: String?,
                                          localUri: String?) {

        hymnDatabase.hymnDao().updateSheetMusicStatus(hymnId, remoteUri, localUri, downloadStatus, progress)
    }

    override fun updateHymnMidiPath(hymnId: Int, midiPath: String) {
        hymnDatabase.hymnDao().updateHymnMidiPath(hymnId, midiPath)
    }

    override fun loadAllTopics(): Observable<List<Topic>> {
        return hymnDatabase.topicDao().getAllTopics().toObservable()
    }

}