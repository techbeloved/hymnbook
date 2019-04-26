package com.techbeloved.hymnbook.data.repo

import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.model.SearchResult
import com.techbeloved.hymnbook.data.repo.local.HymnsDatabase
import com.techbeloved.hymnbook.di.SingletonHolder
import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.hymndetail.BY_TITLE
import com.techbeloved.hymnbook.hymndetail.SortBy
import io.reactivex.Flowable

class HymnsRepositoryImp (private val hymnDatabase: HymnsDatabase) : HymnsRepository {

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

}