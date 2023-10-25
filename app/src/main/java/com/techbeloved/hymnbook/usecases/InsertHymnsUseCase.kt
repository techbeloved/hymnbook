package com.techbeloved.hymnbook.usecases

import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.repo.local.HymnDao
import javax.inject.Inject

class InsertHymnsUseCase @Inject constructor(private val hymnDao: HymnDao) {
    operator fun invoke(values: List<Hymn>) = hymnDao.insertAll(values)
}
