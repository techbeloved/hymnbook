package com.techbeloved.hymnbook.shared.songshare

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.ui.detail.SongDetailScreen

@Composable
internal fun SongShareHandler(
    navController: NavHostController,
) {
    val extractSongShareDataUseCase = remember { appComponent.extractSongShareDataUseCase() }
    val searchSongsUseCase = remember { appComponent.searchSongsUseCase() }
    val deeplink by DeeplinkHandler.deeplinks.collectAsStateWithLifecycle()

    LaunchedEffect(deeplink) {
        val songShareData = deeplink?.let { extractSongShareDataUseCase(it) }
        if (songShareData != null) {
            if (!songShareData.songQuery.isNullOrBlank() || !songShareData.songEntry.isNullOrBlank()) {

                val searchQuery = songShareData.songQuery ?: songShareData.songEntry
                val songbook = songShareData.songbook
                val results = searchSongsUseCase(
                    searchQuery = checkNotNull(searchQuery),
                    songbook = songbook,
                )
                if (results.isNotEmpty()) {
                    val songFound = results.first()
                    navController.navigate(
                        SongDetailScreen(
                            initialSongId = songFound.id,
                            topics = emptyList(),
                            songbooks = songbook?.let { listOf(it) } ?: emptyList(),
                            playlistIds = emptyList(),
                            orderByTitle = false,
                        )
                    )
                }
            }
        }
        DeeplinkHandler.clearDeeplink()
    }
}
