package com.techbeloved.hymnbook.shared.ui.topics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.topics.GetAllTopicsUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import me.tatarka.inject.annotations.Inject

internal class TopicsViewModel @Inject constructor(
    private val getAllTopicsUseCase: GetAllTopicsUseCase,
) : ViewModel() {

    val state = flow { emit(getAllTopicsUseCase().toImmutableList()) }
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = persistentListOf(),
        )

    companion object {
        val Factory = viewModelFactory {
            initializer {
                appComponent.topicsViewModel()
            }
        }
    }
}
