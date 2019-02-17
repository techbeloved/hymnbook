package com.techbeloved.hymnbook.hymnlisting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techbeloved.hymnbook.data.model.HymnTitle
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.usecases.Lce

class HymnListingViewModel(private val hymnsRepository: HymnsRepository) : ViewModel() {
    private val hymnTitlesLiveData_ = MutableLiveData<Lce<List<HymnTitle>>>()
    val hymnTitlesLiveData: LiveData<Lce<List<HymnTitle>>>
        get() = hymnTitlesLiveData_

    fun loadHymnTitles() {
        hymnsRepository.loadHymnTitles()
    }

}
