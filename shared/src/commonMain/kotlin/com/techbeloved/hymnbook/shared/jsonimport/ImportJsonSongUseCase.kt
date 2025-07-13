package com.techbeloved.hymnbook.shared.jsonimport

import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.files.AssetFileSourceProvider
import com.techbeloved.hymnbook.shared.model.assetimport.AssetType
import com.techbeloved.hymnbook.shared.model.assetimport.BundledAsset
import com.techbeloved.hymnbook.shared.model.jsonimport.JsonSongbook
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import okio.buffer
import okio.use

internal class ImportJsonSongUseCase @Inject constructor(
    private val dispatchersProvider: DispatchersProvider,
    private val saveJsonSongUseCase: SaveJsonSongUseCase,
    private val importJsonMediaUseCase: ImportJsonMediaUseCase,
    private val defaultAssetFileSourceProvider: AssetFileSourceProvider,
    private val importSongMusicSheetsUseCase: ImportSongMusicSheetsUseCase,
    private val json: Json,
) {
    suspend operator fun invoke(jsonAssetFile: String) = withContext(dispatchersProvider.io()) {
        runCatching {
            val lyricsJsonContent =
                defaultAssetFileSourceProvider.get(jsonAssetFile).use { fileSource ->
                    fileSource.buffer().use { bufferedSource ->
                        bufferedSource.readUtf8()
                    }
                }

            val jsonSongbook = json.decodeFromString(
                deserializer = JsonSongbook.serializer(),
                string = lyricsJsonContent
            )
            saveJsonSongUseCase(jsonSongbook)
            importSongMusicSheetsUseCase(
                sheetMusicArchiveAsset = BundledAsset(
                    path = jsonSongbook.metadata.sheetMusicArchive,
                    type = AssetType.ZIP,
                ),
                prefix = jsonSongbook.metadata.sheetMusicPrefix,
                songbook = jsonSongbook.metadata.title,
            )
            importJsonMediaUseCase(
                jsonAssetFile = BundledAsset(
                    path = jsonSongbook.metadata.tunesArchive,
                    type = AssetType.ZIP,
                ),
                prefix = jsonSongbook.metadata.tunesPrefix,
                songbook = jsonSongbook.metadata.title,
            )
        }
    }
}
