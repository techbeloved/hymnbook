package com.techbeloved.hymnbook.data.repo.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnDetail
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.model.SearchResult
import io.reactivex.Flowable

@Dao
interface HymnDao {
    @Query("SELECT * FROM hymns ORDER BY title ASC")
    fun getAllHymns(): Flowable<List<Hymn>>

    @Query("SELECT * FROM hymns WHERE num = :number")
    fun getHymnByNumber(number: Int): Flowable<Hymn>

    @Query("SELECT * FROM hymn_titles ORDER BY num ASC")
    fun getAllHymnTitles(): Flowable<List<HymnTitle>>

    @Query("SELECT * FROM hymn_titles  WHERE topicId = :topicId ORDER BY num ASC")
    fun getAllHymnTitlesForTopic(topicId: Int): Flowable<List<HymnTitle>>

    @Query("SELECT * FROM hymn_titles ORDER BY title ASC")
    fun getAllHymnTitlesSortedByTitles(): Flowable<List<HymnTitle>>

    @Query("SELECT * FROM hymn_titles WHERE topicId = :topicId ORDER BY title ASC ")
    fun getAllHymnTitlesSortedByTitlesForTopic(topicId: Int): Flowable<List<HymnTitle>>

    @Query("SELECT * FROM hymn_titles WHERE num IN (:ids)")
    fun getHymnTitlesForIndices(ids: List<Int>): Flowable<List<HymnTitle>>

    @Query("SELECT * FROM hymn_titles WHERE num IN (:indices) ORDER BY title ASC")
    fun getHymnTitlesForIndicesByTitle(indices: List<Int>): Flowable<List<HymnTitle>>


    @Query("SELECT * FROM hymn_with_topics WHERE num = :hymnNo")
    fun getHymnDetail(hymnNo: Int): Flowable<HymnDetail>

    /**
     * This should be used at the end of download or where updating all fields is necessary
     */
    @Query("UPDATE hymns SET downloadStatus = :dStatus, downloadProgress = :dProgress, remoteUri = :remoteUri, localUri = :localUri WHERE num = :hymnNo")
    fun updateSheetMusicStatus(hymnNo: Int, remoteUri: String?, localUri: String?, dStatus: Int, dProgress: Int)

    /**
     * This should be used to update just the progress
     */
    @Query("UPDATE hymns SET downloadStatus = :dStatus, downloadProgress = :dProgress WHERE num = :hymnNo")
    fun updateSheetMusicDownloadProgress(hymnNo: Int, dStatus: Int, dProgress: Int)

    @Query("UPDATE hymns SET downloadStatus = :dStatus WHERE num = :hymnNo")
    fun updateSheetMusicDownloadProgress(hymnNo: Int, dStatus: Int)


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

    @Query("SELECT num FROM hymns WHERE topicId=:topicId ORDER BY num ASC")
    fun getIndicesByNumberForTopic(topicId: Int): Flowable<List<Int>>

    @Query("SELECT num FROM hymns ORDER BY title ASC")
    fun getIndicesByTitle(): Flowable<List<Int>>

    @Query("SELECT num FROM hymns WHERE topicId=:topicId ORDER BY title ASC")
    fun getIndicesByTitleForTopic(topicId: Int): Flowable<List<Int>>

    @Query("SELECT hymns.num, hymns.title, hymns.verses, hymns.chorus FROM hymns JOIN hymnSearchFts ON (hymns.rowid = hymnSearchFts.docid) WHERE hymnSearchFts MATCH :query")
    fun searchHymns(query: String): Flowable<List<SearchResult>>

    @Query("UPDATE hymns SET midi = :midiPath WHERE num = :hymnId")
    fun updateHymnMidiPath(hymnId: Int, midiPath: String)
}