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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.model.SongTitle
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.listing.HymnListingUi
import com.techbeloved.hymnbook.shared.ui.search.HomeSearchBar
import com.techbeloved.hymnbook.shared.ui.search.SearchScreen
import kotlinx.collections.immutable.ImmutableList

internal object HomeScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { appComponent.homeScreenModel() }
        val state by screenModel.state.collectAsState()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
        val navigator = LocalNavigator.currentOrThrow
        Scaffold(
            topBar = {
                AppTopBar(
                    showUpButton = false,
                    titleContent = {
                        HomeSearchBar {
                            navigator.push(SearchScreen)
                        }
                    },
                )
            }
        ) { innerPadding ->
            HomeUi(
                state = state,
                modifier = Modifier
                    .consumeWindowInsets(innerPadding)
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
