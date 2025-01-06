package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.techbeloved.hymnbook.shared.model.SongPageEntry
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.theme.crimsonText
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.launch

internal class SongDetailScreen(private val songbook: String, private val entry: String) : Screen {
    @Composable
    override fun Content() {
        val pagerModel =
            rememberScreenModel { SongDetailPagerModel(songbook, entry) }
        val pagerState by pagerModel.state.collectAsState()
        when (val state = pagerState) {
            is SongDetailPagerState.Content -> {
                SongPager(state,
                    onPageChanged = pagerModel::onPageSelected,
                    pageContent = { pageEntry, contentPadding ->
                        val screenModel =
                            rememberScreenModel(pageEntry.toString()) {
                                SongDetailScreenModel(
                                    pageEntry.id
                                )
                            }
                        val uiDetail by screenModel.state.collectAsState()
                        SongDetailUi(
                            state = uiDetail,
                            contentPadding = contentPadding,
                        )
                    })
            }

            SongDetailPagerState.Loading -> {
                Surface(Modifier.fillMaxSize()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }
        }

    }
}

@Composable
private fun SongDetailUi(
    state: SongUiDetail,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding(),
            ),
    ) {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Text(
                state.content,
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp),
                fontFamily = crimsonText,
                fontSize = 18.sp,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
private fun SongPager(
    state: SongDetailPagerState.Content,
    pageContent: @Composable (entry: SongPageEntry, contentPadding: PaddingValues) -> Unit,
    onPageChanged: (newPage: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hazeState = remember { HazeState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pagerState = rememberPagerState(state.initialPage, pageCount = { state.pages.size })
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(pagerState.settledPage) {
        val currentPage = pagerState.settledPage
        onPageChanged(currentPage)
    }
    Scaffold(
        topBar = {
            AppTopBar(
                title = "",
                scrollBehaviour = scrollBehavior,
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
                modifier = Modifier.hazeChild(hazeState, style = HazeMaterials.ultraThin()),
            ) {
                BottomControlsUi(
                    audioItem = state.audioItem,
                    title = "${state.currentEntry.songBook.songbook}, ${state.currentEntry.songBook.entry}",
                    onPreviousButtonClick = {
                        val currentPage = pagerState.currentPage
                        if (currentPage > 0) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(currentPage - 1)
                            }
                        }
                    },
                    onNextButtonClick = {
                        val currentPage = pagerState.currentPage
                        if (currentPage < pagerState.pageCount - 1) {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(currentPage + 1)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            key = { state.pages[it].id },
            modifier = modifier.consumeWindowInsets(innerPadding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .haze(hazeState),
        ) { page ->
            pageContent(state.pages[page], innerPadding)
        }
    }
}
