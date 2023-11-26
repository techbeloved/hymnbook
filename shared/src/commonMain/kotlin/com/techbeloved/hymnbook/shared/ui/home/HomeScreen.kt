package com.techbeloved.hymnbook.shared.ui.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.techbeloved.hymnbook.shared.model.HymnItem
import com.techbeloved.hymnbook.shared.ui.listing.HymnListingUi
import kotlinx.collections.immutable.ImmutableList

@Composable
internal fun HomeScreen(
    screenModel: HomeScreenModel,
) {
    val state by screenModel.state.collectAsState()
    HomeUi(state)
}

@Composable
internal fun HomeUi(
    state: ImmutableList<HymnItem>,
    modifier: Modifier = Modifier,
) {
    HymnListingUi(state, modifier = modifier.fillMaxSize())
}
