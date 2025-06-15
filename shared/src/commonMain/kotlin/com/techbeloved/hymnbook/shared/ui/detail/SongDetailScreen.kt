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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.model.SongFilter
import com.techbeloved.hymnbook.shared.ui.AppTopBar
import com.techbeloved.hymnbook.shared.ui.settings.NowPlayingSettingsBottomSheet
import com.techbeloved.hymnbook.shared.ui.utils.toUiDetail
import com.techbeloved.media.PlaybackController
import com.techbeloved.media.PlaybackState
import com.techbeloved.media.rememberPlaybackController
import com.techbeloved.media.rememberPlaybackState
import com.techbeloved.sheetmusic.SheetMusicUi
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import dev.chrisbanes.haze.materials.HazeMaterials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
internal data class SongDetailScreen(
    val initialSongId: Long,
    val topics: List<String>,
    val songbooks: List<String>,
    val playlistIds: List<Long>,
    val orderByTitle: Boolean,
) {
    val songFilter
        get() = SongFilter(
            topics = topics,
            songbooks = songbooks,
            playlistIds = playlistIds,
            orderByTitle = orderByTitle,
        )
}

@Composable
internal fun SongDetailScreen(
    onAddSongToPlaylist: (songId: Long) -> Unit,
    pagerViewModel: SongDetailPagerModel = viewModel(
        factory = SongDetailPagerModel.Factory,
    ),
) {
    var currentSongId by remember { mutableStateOf<Long?>(null) }
    val pagerState by pagerViewModel.state.collectAsState()
    val playbackState = rememberPlaybackState()
    val playbackController = rememberPlaybackController(playbackState)
    when (val state = pagerState) {
        is SongDetailPagerState.Content -> {
            SongPager(
                state = state,
                pageContent = { songId, contentPadding ->
                    LaunchedEffect(songId) {
                        currentSongId = songId
                    }
                    val screenModel: SongDetailScreenModel = viewModel(
                        key = songId.toString(),
                        factory = SongDetailScreenModel.Factory,
                        extras = MutableCreationExtras().apply {
                            set(SongDetailScreenModel.SONG_ID_KEY, songId)
                        },
                    )
                    val uiDetail by screenModel.state.collectAsState()
                    SongDetailUi(
                        state = uiDetail,
                        contentPadding = contentPadding,
                    )
                },
                onPageChanged = pagerViewModel::onPageSelected,
                onShowSettingsBottomSheet = pagerViewModel::onShowSettings,
                playbackState = playbackState,
                controller = playbackController,
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

    val bottomSheetState by pagerViewModel.bottomSheetState.collectAsState(context = Dispatchers.Main.immediate)
    when (val state = bottomSheetState) {
        DetailBottomSheetState.Hidden -> {
            // BottomSheet is hidden
        }

        is DetailBottomSheetState.Show -> {
            NowPlayingSettingsBottomSheet(
                onDismiss = pagerViewModel::onHideSettings,
                onSpeedUp = {
                    playbackController?.changePlaybackSpeed(
                        speed = changeMusicSpeed(
                            currentSpeed = playbackState.playbackSpeed,
                            isIncrease = true,
                        ),
                    )
                },
                onSpeedDown = {
                    playbackController?.changePlaybackSpeed(
                        speed = changeMusicSpeed(
                            currentSpeed = playbackState.playbackSpeed,
                            isIncrease = false,
                        ),
                    )
                },
                onZoomOut = pagerViewModel::onDecreaseFontSize,
                onZoomIn = pagerViewModel::onIncreaseFontSize,
                onChangeSongDisplayMode = pagerViewModel::onChangeSongDisplayMode,
                preferences = state.preferences,
                playbackSpeed = playbackState.playbackSpeed,
                onAddSongToPlaylist = {
                    pagerViewModel.onHideSettings()
                    currentSongId?.let { onAddSongToPlaylist(it) }
                },
                onToggleLooping = {
                    playbackController?.toggleLooping()
                },
                isLooping = playbackState.isLooping,
                isLoopingSupported = playbackController?.isLoopingSupported == true,
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
            if (state.content != null) {

                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
                    Text(
                        text = state.content.toUiDetail(state.fontSize.sp),
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        lineHeight = 28.sp,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalHazeMaterialsApi::class)
@Composable
private fun SongPager(
    state: SongDetailPagerState.Content,
    pageContent: @Composable (songId: Long, contentPadding: PaddingValues) -> Unit,
    onPageChanged: (newPage: Int) -> Unit,
    onShowSettingsBottomSheet: () -> Unit,
    playbackState: PlaybackState,
    controller: PlaybackController?,
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
                scrollBehaviour = scrollBehavior,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .5f),
                modifier = Modifier.hazeEffect(hazeState, style = HazeMaterials.ultraThin()),
                title = state.currentSongBookEntry?.songbook.orEmpty(),
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = .8f),
                modifier = Modifier.hazeEffect(hazeState, style = HazeMaterials.ultraThin()),
            ) {
                BottomControlsUi(
                    audioItem = state.audioItem,
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
                    playbackState = playbackState,
                    controller = controller,
                    onShowSettingsBottomSheet = onShowSettingsBottomSheet,
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize()) {
            HorizontalPager(
                state = pagerState,
                key = { state.pages[it] },
                modifier = modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .hazeSource(hazeState),
            ) { page ->
                pageContent(state.pages[page], innerPadding)
            }
        }
    }
}
