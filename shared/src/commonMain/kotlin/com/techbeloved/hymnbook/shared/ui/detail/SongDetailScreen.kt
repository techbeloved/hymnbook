package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.techbeloved.hymnbook.shared.model.SongPageEntry
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.theme.crimsonText

internal class SongDetailScreen(private val songbook: String, private val entry: String) : Screen {
    @Composable
    override fun Content() {
        val pagerModel =
            rememberScreenModel { SongDetailPagerModel(songbook, entry) }
        val pagerState by pagerModel.state.collectAsState()
        when (val state = pagerState) {
            is SongDetailPagerState.Content -> {
                SongPager(state, pageContent = { pageEntry ->
                    val screenModel =
                        rememberScreenModel(pageEntry.toString()) { SongDetailScreenModel(pageEntry.id) }
                    val uiDetail by screenModel.state.collectAsState()
                    SongDetailUi(uiDetail)
                })
            }

            SongDetailPagerState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

    }
}

@Composable
private fun SongDetailUi(state: SongUiDetail, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .safeContentPadding(),
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Text(
                state.content,
                modifier = Modifier.fillMaxWidth(),
                fontFamily = crimsonText,
                fontSize = 18.sp,
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SongPager(
    state: SongDetailPagerState.Content,
    pageContent: @Composable (entry: SongPageEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            AppTopBar("", scrollBehaviour = scrollBehavior)
        },
    ) { paddingValues ->
        HorizontalPager(
            state = rememberPagerState(state.initialPage, pageCount = { state.pages.size }),
            modifier = modifier.padding(paddingValues),
            key = { state.pages[it].id }
        ) { page ->
            pageContent(state.pages[page])
        }
    }
}
