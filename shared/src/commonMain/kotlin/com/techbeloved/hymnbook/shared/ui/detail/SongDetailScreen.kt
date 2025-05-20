@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.model.SongPageEntry
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.settings.NowPlayingSettingsBottomSheet
import com.techbeloved.hymnbook.shared.ui.theme.crimsonText
import com.techbeloved.sheetmusic.SheetMusicUi
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
internal data class SongDetailScreen(val songbook: String, val entry: String)

@Composable
internal fun SongDetailScreen(
    pagerViewModel: SongDetailPagerModel = viewModel(
        factory = SongDetailPagerModel.Factory,
    ),
) {
    val pagerState by pagerViewModel.state.collectAsState()
    when (val state = pagerState) {
        is SongDetailPagerState.Content -> {
            SongPager(
                state,
                pageContent = { pageEntry, contentPadding ->
                    val screenModel: SongDetailScreenModel = viewModel(
                        key = pageEntry.toString(),
                        factory = SongDetailScreenModel.Factory,
                        extras = MutableCreationExtras().apply {
                            set(SongDetailScreenModel.SONG_ID_KEY, pageEntry.id)
                        },
                    )
                    val uiDetail by screenModel.state.collectAsState()
                    SongDetailUi(
                        state = uiDetail,
                        contentPadding = contentPadding,
                    )
                },
                onPageChanged = pagerViewModel::onPageSelected,
                onChangeSongDisplayMode = pagerViewModel::onChangeSongDisplayMode,
                onShowSettingsBottomSheet = pagerViewModel::onShowSettings,
            )
        }

        SongDetailPagerState.Loading -> {
            Surface(Modifier.fillMaxSize()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    val bottomSheetState by pagerViewModel.bottomSheetState.collectAsState()
    when (bottomSheetState) {
        DetailBottomSheetState.Hidden -> {
            // BottomSheet is hidden
        }

        is DetailBottomSheetState.Show -> {
            NowPlayingSettingsBottomSheet(
                onDismiss = pagerViewModel::onHideSettings,
                onZoomIn = pagerViewModel::onIncreaseFontSize,
                onZoomOut = pagerViewModel::onDecreaseFontSize,
            )
        }
    }
}

@Composable
private fun SongDetailUi(
    state: SongUiDetail,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    if (state.songDisplayMode == SongDisplayMode.SheetMusic && state.sheetMusic != null) {
        SheetMusicUi(
            sheetMusicItem = state.sheetMusic,
            modifier = modifier.fillMaxSize(),
        )
    } else {
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
                    fontSize = state.fontSize.sp,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
private fun SongPager(
    state: SongDetailPagerState.Content,
    pageContent: @Composable (entry: SongPageEntry, contentPadding: PaddingValues) -> Unit,
    onPageChanged: (newPage: Int) -> Unit,
    onChangeSongDisplayMode: (mode: SongDisplayMode) -> Unit,
    onShowSettingsBottomSheet: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hazeState = remember { HazeState() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val pagerState = rememberPagerState(state.initialPage, pageCount = { state.pages.size })
    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(pagerState.settledPage) {
        val currentPage = pagerState.settledPage
        onPageChanged(currentPage)
    }
    Scaffold(
        topBar = {
            AppTopBar(
                titleContent = {
                    SingleChoiceSegmentedButtonRow {
                        state.displayModes.forEachIndexed { index, displayModeState ->
                            SegmentedButton(
                                selected = displayModeState.displayMode == state.currentDisplayMode,
                                onClick = { onChangeSongDisplayMode(displayModeState.displayMode) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = state.displayModes.size
                                ),
                                enabled = displayModeState.isEnabled,
                            ) {
                                Text(text = displayModeState.text)
                            }
                        }
                    }
                },
                scrollBehaviour = scrollBehavior,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .5f),
                modifier = Modifier.hazeEffect(hazeState, style = HazeMaterials.ultraThin()),
                actions = {
                    IconButton(onClick = onShowSettingsBottomSheet) {
                        Icon(
                            imageVector = Icons.Rounded.Settings,
                            contentDescription = "Show settings",
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent,
                modifier = Modifier.hazeEffect(hazeState, style = HazeMaterials.ultraThin()),
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
            modifier = modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .hazeSource(hazeState),
        ) { page ->
            pageContent(state.pages[page], innerPadding)
        }
    }
}
