package com.techbeloved.hymnbook.hymndetail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.techbeloved.hymnbook.data.SharedPreferencesRepo
import com.techbeloved.hymnbook.data.model.NightMode
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class QuickSettingsViewModel @Inject constructor(private val sharedPreferencesRepo: SharedPreferencesRepo): ViewModel() {

    private val disposables = CompositeDisposable()
    val nightMode: MutableLiveData<NightMode> = MutableLiveData()

    val enableSheetMusic: MutableLiveData<Boolean> = MutableLiveData()

    init {
        getNightModeData()
        getSheetMusicPreference()
    }

    private fun getSheetMusicPreference() {
        sharedPreferencesRepo.preferSheetMusic()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(enableSheetMusic::setValue, Timber::w)
            .let(disposables::add)
    }

    private fun getNightModeData() {
        sharedPreferencesRepo.nightModeActive().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(nightMode::setValue) { Timber.w(it) }
            .let { disposables.add(it) }
    }

    override fun onCleared() {
        disposables.clear()
        super.onCleared()
    }

    fun setNightMode(mode: NightMode) {
        sharedPreferencesRepo.setNightModeActive(mode)
    }

    fun preferSheetMusic(checked: Boolean) {
        sharedPreferencesRepo.updatePreferSheetMusic(checked)
    }
}
