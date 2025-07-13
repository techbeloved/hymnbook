package com.techbeloved.hymnbook.shared.analytics

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

internal class SetAnalyticsDefaultParametersUseCase @Inject constructor(private val appAnalytics: AppAnalytics) {
    suspend operator fun invoke(params: Map<String, String>) = withContext(Dispatchers.IO) {
        appAnalytics.setDefaultParams(params)
    }
}
