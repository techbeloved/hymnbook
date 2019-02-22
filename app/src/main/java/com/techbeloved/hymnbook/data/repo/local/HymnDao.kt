package com.techbeloved.hymnbook.data.repo.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.model.HymnTitle
import io.reactivex.Flowable

@Dao
interface HymnDao {
    @Query("SELECT * FROM hymns ORDER BY title ASC")
    fun getAllHymns(): Flowable<List<Hymn>>

    @Query("SELECT * FROM hymns WHERE num = :number")
    fun getHymnByNumber(number: Int): Flowable<Hymn>

    @Query("SELECT * FROM hymn_titles ORDER BY num ASC")
    fun getAllHymnTitles(): Flowable<List<HymnTitle>>

    @Query("SELECT * FROM hymn_with_topics WHERE num = :hymnNo")
    fun getHymnDetail(hymnNo: Int): Flowable<HymnDetail>

    @Insert
    fun insertAll(hymns: List<Hymn>)

    @Insert
    fun insert(hymn: Hymn)

    @Delete
    fun delete(hymn: Hymn)

    @Query("DELETE FROM hymns WHERE num = :hymnNo")
    fun deleteByNumber(hymnNo: Int)

    @Query("DELETE FROM hymns")
    fun deleteAll()

    @Query("SELECT num FROM hymns ORDER BY num ASC")
    fun getIndicesByNumber(): Flowable<List<Int>>

    @Query("SELECT num FROM hymns ORDER BY title ASC")
    fun getIndicesByTitle(): Flowable<List<Int>>
}