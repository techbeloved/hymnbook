package com.techbeloved.hymnbook.usecases

import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import io.reactivex.Observable
import javax.inject.Inject

class HymnbookUseCasesImp @Inject constructor(
    private val preferencesRepo: SharedPreferencesRepo,
) : HymnbookUseCases {

    override fun appFirstStart(): Observable<Boolean> {
        return preferencesRepo.isFirstStart()
    }

    override fun updateAppFirstStart(firstStart: Boolean) {
        preferencesRepo.setFirstStart(false)
    }
}
