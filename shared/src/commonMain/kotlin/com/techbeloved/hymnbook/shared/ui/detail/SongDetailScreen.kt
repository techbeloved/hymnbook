@file:OptIn(ExperimentalMaterial3Api::class)

package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel
import com.techbeloved.hymnbook.shared.config.defaultAppConfig
import com.techbeloved.hymnbook.shared.generated.Res
import com.techbeloved.hymnbook.shared.generated.content_description_search
import com.techbeloved.hymnbook.shared.generated.content_description_show_more_controls
import com.techbeloved.hymnbook.shared.generated.no_sheet_music_available
import com.techbeloved.hymnbook.shared.generated.show_lyrics
import com.techbeloved.hymnbook.shared.model.SongDisplayMode
import com.techbeloved.hymnbook.shared.model.SongFilter
import com.techbeloved.hymnbook.shared.songs.CopyrightStatus
import com.techbeloved.hymnbook.shared.songs.SongData
import com.techbeloved.hymnbook.shared.ui.CenteredAppTopBar
import com.techbeloved.hymnbook.shared.ui.settings.NowPlayingSettingsBottomSheet
import com.techbeloved.hymnbook.shared.ui.share.NativeShareButton
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
import io.ktor.http.URLBuilder
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource

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
    val soundFontStateAvailable = soundFontState as? SoundFontState.Available
    val isSoundFontSupported = soundFontState !is SoundFontState.NotSupported

    val playbackController = rememberPlaybackController(
        playbackState = playbackState,
        midiSoundFontPath = soundFontStateAvailable?.soundFont?.fileHash?.path,
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
                        onShowLyrics = {
                            pagerViewModel.onChangeSongDisplayMode(SongDisplayMode.Lyrics)
                        },
                    )
                },
                onPageChanged = pagerViewModel::onPageSelected,
                onShowSettingsBottomSheet = pagerViewModel::onShowSettings,
                playbackState = playbackState,
                controller = playbackController,
                onOpenSearch = onOpenSearch,
                onShowSoundFontSettings = onShowSoundFontSettings,
            )

            when (val state = state.bottomSheetState) {
                DetailBottomSheetState.Hidden -> {
                    // BottomSheet is hidden
                }

                is DetailBottomSheetState.Show -> {
                    NativeShareButton {
                        NowPlayingSettingsBottomSheet(
                            onShareSongClick = {
                                onClick(state.shareAppData)
                            },
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
                            onSoundfonts = onShowSoundFontSettings,
                            onAddSongToPlaylist = {
                                pagerViewModel.onHideSettings()
                                currentSongId?.let { onAddSongToPlaylist(it) }
                            },
                            onToggleLooping = {
                                playbackController?.toggleLooping()
                            },
                            isSoundfontSupported = isSoundFontSupported,
                            isLooping = playbackState.isLooping,
                            isLoopingSupported = playbackController?.isLoopingSupported == true,
                            preferences = state.preferences,
                            playbackSpeed = playbackState.playbackSpeed,
                        )
                    }
                }
            }


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

@Composable
private fun SongDetailUi(
    state: SongUiDetail,
    contentPadding: PaddingValues,
    onShowLyrics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    when {
        shouldEnforceCopyright(state.content?.copyright) -> {
            CopyrightSongNotAvailableUi(
                modifier = modifier.fillMaxSize()
                    .padding(contentPadding),
                onOnlineSearchButtonClick = {
                   state.content?.let { getSongGoogleSearchUrl(it) }?.let { uri ->
                       uriHandler.openUri(uri)
                   }
                },
            )
        }

        state.songDisplayMode == SongDisplayMode.SheetMusic -> {
            if (state.sheetMusic != null) {
                SheetMusicUi(
                    sheetMusicItem = state.sheetMusic,
                    modifier = modifier.fillMaxSize()
                        .padding(contentPadding),
                )
            } else {
                SheetMusicNotAvailable(
                    modifier = modifier.fillMaxSize().padding(16.dp),
                    onShowLyrics = onShowLyrics,
                )
            }
        }

        else -> LyricsUi(
            modifier = modifier.padding(contentPadding),
            state = state,
        )
    }
}

@Composable
private fun SheetMusicNotAvailable(
    onShowLyrics: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(Res.string.no_sheet_music_available),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onShowLyrics) {
            Text(text = stringResource(Res.string.show_lyrics))
        }
    }
}

@Composable
private fun LyricsUi(
    state: SongUiDetail,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        if (state.content != null) {

            // All these to calculate the line height based on the font size
            val textStyle = MaterialTheme.typography.bodyLarge
            val defaultFontSize = textStyle.fontSize
            val fontSize = defaultFontSize * state.fontSizeMultiplier
            CompositionLocalProvider(
                LocalContentColor provides MaterialTheme.colorScheme.onSurface,
                LocalTextStyle provides textStyle.merge(
                    fontSize = fontSize,
                    lineHeight = LineHeightMultiplier.em,
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
            CenteredAppTopBar(
                scrollBehaviour = scrollBehavior,
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = .5f),
                modifier = Modifier.hazeEffect(hazeState, style = HazeMaterials.ultraThin()),
                titleContent = {
                    val title = "${state.currentSongBookEntry?.songbook}"
                    Text(
                        text = title,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier,
                    )
                },
                subtitleContent = {
                    Text(text = "Hymn, ${state.currentSongBookEntry?.entry}")
                },
                actions = {
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = onOpenSearch, modifier = Modifier) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = stringResource(Res.string.content_description_search),
                        )
                    }
                    IconButton(
                        onClick = onShowSettingsBottomSheet,
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = stringResource(Res.string.content_description_show_more_controls),
                        )
                    }
                }
            )
        },
        bottomBar = {
            var progressVisible by remember { mutableStateOf(false) }
            var progress by remember { mutableFloatStateOf(0f) }

            LaunchedEffect(playbackState) {
                snapshotFlow {
                    object {
                        val progress = playbackState.position.toFloat() / playbackState.duration
                        val isPlaying = playbackState.isPlaying
                    }
                }.collect {
                    progress = it.progress
                    progressVisible = it.isPlaying
                }
            }
            Column {
                AnimatedVisibility(visible = progressVisible) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(1.dp),
                        gapSize = 2.dp,
                    )
                }

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
                        isSoundFontDownloadRequired = state.soundFontState is SoundFontState.NotAvailable,
                        onShowSoundFontSettings = onShowSoundFontSettings,
                    )
                }
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

private fun shouldEnforceCopyright(
    copyright: String?,
): Boolean = (!copyright.isNullOrBlank()
        && !CopyrightStatus.PUBLIC_DOMAIN.contentEquals(copyright, ignoreCase = true)
        && defaultAppConfig.enforceCopyrightRestriction)

private fun getSongGoogleSearchUrl(songData: SongData): String? {
    val title = songData.title
    val firstLine = songData.lyrics.firstOrNull()
        ?.content?.substringBefore("\n")
    return if (firstLine != null) {
        val searchQuery = "$title $firstLine hymn lyrics"
        URLBuilder("https://www.google.com/search")
            .apply {
                parameters.append("q", searchQuery)
            }.buildString()
    } else {
        null
    }
}
