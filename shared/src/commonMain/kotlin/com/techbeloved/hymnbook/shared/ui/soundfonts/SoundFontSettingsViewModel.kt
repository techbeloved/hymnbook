package com.techbeloved.hymnbook.shared.ui.soundfonts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.ext.absoluteFileUrl
import com.techbeloved.hymnbook.shared.files.DownloadFileUseCase
import com.techbeloved.hymnbook.shared.files.HashFileUseCase
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.model.soundfont.OnlineSoundFont
import com.techbeloved.hymnbook.shared.model.soundfont.SavedSoundFont
import com.techbeloved.hymnbook.shared.preferences.ChangePreferenceUseCase
import com.techbeloved.hymnbook.shared.soundfont.GetOnlineSoundFontsUseCase
import com.techbeloved.hymnbook.shared.soundfont.GetSavedSoundFontsUseCase
import com.techbeloved.hymnbook.shared.soundfont.GetSoundFontPreferenceFlowUseCase
import com.techbeloved.hymnbook.shared.soundfont.SaveDownloadedSoundFontUseCase
import com.techbeloved.hymnbook.shared.soundfont.SoundFontPreferenceKey
import com.techbeloved.hymnbook.shared.time.InstantProvider
import com.techbeloved.media.download.MediaDownloadState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.tatarka.inject.annotations.Inject

internal class SoundFontSettingsViewModel @Inject constructor(
    private val saveDownloadedSoundFontUseCase: SaveDownloadedSoundFontUseCase,
    private val getOnlineSoundFontsUseCase: GetOnlineSoundFontsUseCase,
    private val changePreferenceUseCase: ChangePreferenceUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val hashFileUseCase: HashFileUseCase,
    private val instantProvider: InstantProvider,
    private val okioFileSystemProvider: OkioFileSystemProvider,
    private val dispatchersProvider: DispatchersProvider,
    getSoundFontPreferenceFlowUseCase: GetSoundFontPreferenceFlowUseCase,
    getSavedSoundFontsUseCase: GetSavedSoundFontsUseCase,
) : ViewModel() {

    private val downloadState = MutableStateFlow(
        emptyMap<String, MediaDownloadState>()
    )
    val state = combine(
        getSavedSoundFontsUseCase(),
        getSoundFontPreferenceFlowUseCase(),
        flow { emit(getOnlineSoundFontsUseCase()) },
        downloadState,
    ) { saved, preference, online, downloadState ->
        computeNewState(
            saved = saved,
            online = online,
            preference = preference,
            downloadState = downloadState,
        )
    }.stateIn(
        scope = viewModelScope,
        started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
        initialValue = SoundFontSettingsState(
            isLoading = true,
            items = persistentListOf(),
        ),
    )

    private fun computeNewState(
        saved: List<SavedSoundFont>,
        online: List<OnlineSoundFont>,
        preference: SavedSoundFont?,
        downloadState: Map<String, MediaDownloadState>,
    ): SoundFontSettingsState {
        val savedMap = saved.associateBy { it.fileHash.path }

        val fontItems = if (online.isNotEmpty()) {
            online.map { font ->
                SoundFontItem(
                    fileName = font.name,
                    displayName = font.displayName,
                    checksum = font.checksum,
                    isDownloaded = savedMap[font.name]?.fileHash?.path == font.name,
                    isPreferred = preference?.fileHash?.path == font.name,
                    downloadState = downloadState[font.name],
                    downloadUrl = font.downloadUrl,
                    fileSize = font.size,
                )
            }
        } else {
            saved.map { font ->
                SoundFontItem(
                    fileName = font.fileHash.path,
                    displayName = font.displayName,
                    checksum = font.fileHash.sha256,
                    isDownloaded = true,
                    isPreferred = preference?.fileHash?.path == font.fileHash.path,
                    downloadState = downloadState[font.fileHash.path],
                    downloadUrl = "",
                    fileSize = "",
                )
            }
        }
        return SoundFontSettingsState(
            isLoading = false,
            items = fontItems.toImmutableList(),
        )
    }

    fun onDownloadItemClicked(item: SoundFontItem) {
        val downloads = downloadState.value
        val currentDownload = downloads[item.fileName]
        if (currentDownload != null && currentDownload !is MediaDownloadState.Error) {
            return
        }
        downloadState.update { state -> state + (item.fileName to MediaDownloadState.Initializing) }
        downloadSoundFontIfPossible(item)
    }

    private fun downloadSoundFontIfPossible(item: SoundFontItem) {
        viewModelScope.launch {
            val filePath = absoluteFileUrl(relativePath = item.fileName)
            val fileSystem = okioFileSystemProvider.get().fileSystem
            if (fileSystem.exists(filePath)) {
                val fileHash = hashFileUseCase(filePath = filePath)
                if (fileHash.sha256 == item.checksum) {
                    // Already downloaded. Now save
                    saveDownloadedSoundFontUseCase(
                        soundFont = SavedSoundFont(
                            fileHash = fileHash.copy(path = item.fileName),
                            displayName = item.displayName,
                            downloadedDate = instantProvider.get()
                                .toLocalDateTime(TimeZone.currentSystemDefault()),
                            fileSize = item.fileSize,
                        ),
                    )
                    downloadState.update { state -> state + (item.fileName to MediaDownloadState.Success) }
                    return@launch
                } else {
                    withContext(dispatchersProvider.io()) {
                        runCatching { fileSystem.delete(filePath) }
                    }
                }
            }
            downloadFileUseCase(item.downloadUrl, item.fileName).collect { newDownloadState ->
                downloadState.update { it + (item.fileName to newDownloadState) }
                if (newDownloadState is MediaDownloadState.Success) {
                    val fileHash = hashFileUseCase(filePath = filePath)
                    if (fileHash.sha256 == item.checksum) {
                        saveDownloadedSoundFontUseCase(
                            soundFont = SavedSoundFont(
                                fileHash = fileHash.copy(path = item.fileName),
                                displayName = item.displayName,
                                downloadedDate = instantProvider.get()
                                    .toLocalDateTime(TimeZone.currentSystemDefault()),
                                fileSize = item.fileSize,
                            ),
                        )
                    } else {
                        withContext(dispatchersProvider.io()) {
                            runCatching { fileSystem.delete(filePath) }
                        }
                    }
                }
            }
        }
    }

    fun onItemClicked(item: SoundFontItem) {
        viewModelScope.launch {
            changePreferenceUseCase(SoundFontPreferenceKey) {
                item.fileName
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {

            initializer {
                appComponent.soundFontSettingsViewModel()
            }
        }
    }
}
