@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
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
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

private const val LineHeightMultiplier = 1.5f

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
    onOpenSearch: () -> Unit,
    onShowSoundFontSettings: () -> Unit,
    onAddSongToPlaylist: (songId: Long) -> Unit,
    pagerViewModel: SongDetailPagerModel = viewModel(
        factory = SongDetailPagerModel.Factory,
    ),
) {
    LaunchedEffect(Unit) {
        pagerViewModel.onScreenLoaded()
    }
    var currentSongId by remember { mutableStateOf<Long?>(null) }
    val pagerState by pagerViewModel.state.collectAsState()
    val playbackState = rememberPlaybackState()

    // Check if soundfont is available and use it to create a playback controller
    val soundFontState = (pagerState as? SongDetailPagerState.Content)?.soundFontState
            as? SoundFontState.Available

    val playbackController = rememberPlaybackController(
        playbackState = playbackState,
        midiSoundFontPath = soundFontState?.soundFont?.fileHash?.path,
    )

    LaunchedEffect(playbackState.isLooping) {
        pagerViewModel.trackSongLoopingToggle(playbackState.isLooping)
    }
    LaunchedEffect(playbackState.playbackSpeed) {
        pagerViewModel.trackSongSpeed(playbackState.playbackSpeed)
    }
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
                    val uiDetail by screenModel.state.collectAsStateWithLifecycle()
                    SongDetailUi(
                        state = uiDetail,
                        contentPadding = contentPadding,
                    )
                },
                onPageChanged = pagerViewModel::onPageSelected,
                onShowSettingsBottomSheet = pagerViewModel::onShowSettings,
                playbackState = playbackState,
                controller = playbackController,
                onOpenSearch = onOpenSearch,
                onShowSoundFontSettings = onShowSoundFontSettings,
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
            modifier = modifier.fillMaxSize()
                .padding(contentPadding),
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

                // All these to calculate the line height based on the font size
                val textStyle = MaterialTheme.typography.bodyLarge
                val defaultFontSize = textStyle.fontSize
                val fontSize = defaultFontSize * state.fontSizeMultiplier
                val lineHeight = textStyle.lineHeight * LineHeightMultiplier
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurface,
                    LocalTextStyle provides textStyle.merge(
                        fontSize = fontSize,
                        lineHeight = lineHeight,
                        lineHeightStyle = LineHeightStyle(
                            alignment = LineHeightStyle.Alignment.Proportional,
                            trim = LineHeightStyle.Trim.None,
                        ),
                    )
                ) {

                    Text(
                        text = state.content.toUiDetail(fontSize),
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 16.dp),
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
    onShowSoundFontSettings: () -> Unit,
    playbackState: PlaybackState,
    controller: PlaybackController?,
    onOpenSearch: () -> Unit,
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
                actions = {
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = onOpenSearch, modifier = Modifier) {
                        Icon(imageVector = Icons.TwoTone.Search, contentDescription = "Search")
                    }
                }
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
                    isSoundFontDownloadRequired = state.soundFontState is SoundFontState.NotAvailable,
                    onShowSoundFontSettings = onShowSoundFontSettings,
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
                userScrollEnabled = state.currentDisplayMode != SongDisplayMode.SheetMusic,
            ) { page ->
                pageContent(state.pages[page], innerPadding)
            }
        }
    }
}
