@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.techbeloved.hymnbook.shared.model.SongTitle
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.listing.HymnListingUi
import kotlinx.collections.immutable.ImmutableList

internal object HomeScreen : Screen {
    @Composable
    override fun Content() {

        val screenModel = rememberScreenModel { HomeScreenModel() }
        val state by screenModel.state.collectAsState()
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Scaffold(
            topBar = {
                AppTopBar("Hymnbook", showUpButton = false, scrollBehaviour = scrollBehavior)
            },
        ) { innerPadding ->
            HomeUi(
                state = state,
                modifier = Modifier.consumeWindowInsets(innerPadding)
                    .nestedScroll(scrollBehavior.nestedScrollConnection),
                contentPadding = innerPadding,
            )
        }
    }

}

@Composable
internal fun HomeUi(
    state: ImmutableList<SongTitle>,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    HymnListingUi(
        hymnItems = state,
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
    )
}
