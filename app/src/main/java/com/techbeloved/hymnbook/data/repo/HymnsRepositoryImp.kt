package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnAssetUpdate
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.model.HymnNumber
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.model.OnlineMusicUpdate
import com.techbeloved.hymnbook.data.model.SearchResult
import com.techbeloved.hymnbook.data.model.Topic
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.di.SingletonHolder
import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.hymndetail.BY_TITLE
import com.techbeloved.hymnbook.hymndetail.SortBy
import dagger.Reusable
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import javax.inject.Inject

@Reusable
class HymnsRepositoryImp @Inject constructor(private val hymnDatabase: HymnsDatabase) :
    HymnsRepository {
    override fun loadHymnTitlesForIndices(
        indices: List<Int>,
        sortBy: Int
    ): Observable<List<HymnTitle>> {
        return when (sortBy) {
            BY_NUMBER -> hymnDatabase.hymnDao().getHymnTitlesForIndices(indices).toObservable()
            else -> hymnDatabase.hymnDao().getHymnTitlesForIndicesByTitle(indices).toObservable()
        }

    }

    companion object : SingletonHolder<HymnsRepository, HymnsDatabase>(::HymnsRepositoryImp);

    override fun searchHymns(searchTerm: String): Flowable<List<SearchResult>> {
        return hymnDatabase.hymnDao().searchHymns("$searchTerm*")
    }

    override fun getHymnById(hymnNo: Int): Flowable<Hymn> {
        return hymnDatabase.hymnDao().getHymnByNumber(hymnNo)
    }

    override fun loadHymnIndices(sortBy: Int, topicId: Int): Flowable<List<HymnNumber>> {
        return when (sortBy) {
            BY_TITLE -> {
                if (topicId == 0) {
                    hymnDatabase.hymnDao().getIndicesByTitle()
                } else {
                    hymnDatabase.hymnDao().getIndicesByTitleForTopic(topicId)
                }
            }
            BY_NUMBER -> {
                if (topicId == 0) {
                    hymnDatabase.hymnDao().getIndicesByNumber()
                } else {
                    hymnDatabase.hymnDao().getIndicesByNumberForTopic(topicId)
                }
            }
            else -> Flowable.error(Throwable("Unsupported sort type"))
        }
    }

    override fun getHymnDetailByNumber(hymnNo: Int): Flowable<HymnDetail> {
        return hymnDatabase.hymnDao().getHymnDetail(hymnNo)
    }

    override fun loadHymnTitles(@SortBy sortBy: Int, topicId: Int): Flowable<List<HymnTitle>> {
        return when (sortBy) {
            BY_TITLE -> {
                if (topicId == 0) {
                    hymnDatabase.hymnDao().getAllHymnTitlesSortedByTitles()
                } else {
                    hymnDatabase.hymnDao().getAllHymnTitlesSortedByTitlesForTopic(topicId)
                }
            }
            else -> {
                if (topicId == 0) {
                    hymnDatabase.hymnDao().getAllHymnTitles()
                } else {
                    hymnDatabase.hymnDao().getAllHymnTitlesForTopic(topicId)
                }
            }
        }
    }

    override fun updateHymnDownloadProgress(hymnId: Int, progress: Int, downloadStatus: Int) {
        hymnDatabase.hymnDao().updateSheetMusicDownloadProgress(hymnId, downloadStatus, progress)
    }

    override fun updateHymnDownloadStatus(
        hymnId: Int,
        progress: Int,
        downloadStatus: Int,
        remoteUri: String?,
        localUri: String?
    ) {

        hymnDatabase.hymnDao()
            .updateSheetMusicStatus(hymnId, remoteUri, localUri, downloadStatus, progress)
    }

    override fun updateHymnMidiPath(hymnId: Int, midiPath: String) {
        hymnDatabase.hymnDao().updateHymnMidiPath(hymnId, midiPath)
    }

    override fun loadAllTopics(): Observable<List<Topic>> {
        return hymnDatabase.topicDao().getAllTopics().toObservable()
    }

    override fun getTopicById(topicId: Int): Observable<Topic> {
        return hymnDatabase.topicDao().getTopicById(topicId).toObservable()
    }

    override fun synchroniseOnlineMusic(onlineHymns: List<OnlineHymn>): Completable {
        return hymnDatabase.hymnDao()
            .updateWithOnlineMusic(onlineHymns.map { OnlineMusicUpdate(it.id, it.sheetMusicUrl) })
    }

    override fun updateHymnAsset(updates: List<HymnAssetUpdate>): Completable {
        return hymnDatabase.hymnDao().updateHymnAsset(updates)
    }

}