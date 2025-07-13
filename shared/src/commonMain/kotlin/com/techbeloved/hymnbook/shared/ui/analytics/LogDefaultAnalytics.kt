package com.techbeloved.hymnbook.shared.ui.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
internal fun LogDefaultAnalytics(
    viewModel: AnalyticsViewModel = viewModel(factory = AnalyticsViewModel.Factory),
) {
    LaunchedEffect(Unit) {
        viewModel.analyticsTracking.collect(viewModel::logDefaultParameters)
    }
}
