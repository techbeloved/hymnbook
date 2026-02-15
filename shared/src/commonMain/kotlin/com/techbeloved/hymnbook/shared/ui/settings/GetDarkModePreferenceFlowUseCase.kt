package com.techbeloved.hymnbook.shared.ui.settings

import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.preferences.GetPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.settings.DarkModePreference
import com.techbeloved.hymnbook.shared.settings.DarkModePreferenceKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

public interface GetDarkModePreferenceFlowUseCase {
    public operator fun invoke(): Flow<DarkModePreference>

    public companion object {
        public val instance: GetDarkModePreferenceFlowUseCase
            get() = appComponent.getDarkModePreferenceFlowUseCase()
    }
}

internal class GetDarkModePreferenceFlowUseCaseImpl @Inject constructor(
    private val getPreferenceFlowUseCase: GetPreferenceFlowUseCase,
) : GetDarkModePreferenceFlowUseCase {
    override fun invoke(): Flow<DarkModePreference> = getPreferenceFlowUseCase(
        DarkModePreferenceKey
    ).map { DarkModePreference.valueOf(it) }
}
