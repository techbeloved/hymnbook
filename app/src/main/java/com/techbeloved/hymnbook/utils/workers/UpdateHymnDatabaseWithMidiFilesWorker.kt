package com.techbeloved.hymnbook.utils.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.data.model.HymnAssetUpdate
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.usecases.GetBundledCatalogAssetsUseCase
import com.techbeloved.hymnbook.usecases.GetBundledHymnsAssetsUseCase
import com.techbeloved.hymnbook.usecases.GetBundledTopicsAssetsUseCase
import com.techbeloved.hymnbook.usecases.GetBundledTunesAssetsUseCase
import com.techbeloved.hymnbook.usecases.GetCatalogArchivePreferenceUseCase
import com.techbeloved.hymnbook.usecases.GetHymnsArchivePreferenceUseCase
import com.techbeloved.hymnbook.usecases.GetHymnsFromJsonAssetUseCase
import com.techbeloved.hymnbook.usecases.GetMidiArchivePreferenceUseCase
import com.techbeloved.hymnbook.usecases.InsertHymnsUseCase
import com.techbeloved.hymnbook.usecases.InsertTopicsUseCase
import com.techbeloved.hymnbook.usecases.UpdateBundledArchivePreferences
import com.techbeloved.hymnbook.usecases.UpdateHymnsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single
import timber.log.Timber
import java.io.File

@HiltWorker
class UpdateHymnDatabaseWithMidiFilesWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val hymnsRepository: HymnsRepository,
    private val getBundledCatalogAssetsUseCase: GetBundledCatalogAssetsUseCase,
    private val getBundledTunesAssetsUseCase: GetBundledTunesAssetsUseCase,
    private val getMidiArchivePreferenceUseCase: GetMidiArchivePreferenceUseCase,
    private val getCatalogArchivePreferenceUseCase: GetCatalogArchivePreferenceUseCase,
    private val updateBundledArchivePreferences: UpdateBundledArchivePreferences,
    private val getHymnsArchivePreferenceUseCase: GetHymnsArchivePreferenceUseCase,
    private val getBundledHymnsAssetsUseCase: GetBundledHymnsAssetsUseCase,
    private val getHymnsFromJsonAssetUseCase: GetHymnsFromJsonAssetUseCase,
    private val getBundledTopicsAssetsUseCase: GetBundledTopicsAssetsUseCase,
    private val insertHymnsUseCase: InsertHymnsUseCase,
    private val updateHymnsUseCase: UpdateHymnsUseCase,
    private val insertTopicsUseCase: InsertTopicsUseCase,
) : RxWorker(context, params) {
    override fun createWork(): Single<Result> {
        return Single.create { emitter ->
            val bundledTunes = getBundledTunesAssetsUseCase()
            val bundledCatalogs = getBundledCatalogAssetsUseCase()
            val bundledHymns = getBundledHymnsAssetsUseCase()
            val alreadySavedTunes = getMidiArchivePreferenceUseCase()
            val alreadySavedCatalogs = getCatalogArchivePreferenceUseCase()
            val alreadySavedHymns = getHymnsArchivePreferenceUseCase()

            val newTunes = bundledTunes.filter { !alreadySavedTunes.contains(it) }
            val newCatalogs = bundledCatalogs.filter { !alreadySavedCatalogs.contains(it) }
            val newHymns = bundledHymns.filter { !alreadySavedHymns.contains(it) }
            Timber.d("Bundled: $bundledHymns, NewHymns: $newHymns")
            if (newHymns.isNotEmpty()) {
                // Update hymns
                val updatedHymns = newHymns.flatMap { getHymnsFromJsonAssetUseCase(it) }
                val allHymnIndices = hymnsRepository.loadHymnIndices(BY_NUMBER).blockingFirst()
                if (allHymnIndices.isEmpty()) {
                    val topics = getBundledTopicsAssetsUseCase()
                    insertTopicsUseCase(topics).blockingAwait()
                    insertHymnsUseCase(updatedHymns).blockingAwait()
                } else {
                    updateHymnsUseCase(updatedHymns).blockingAwait()
                }
            }
            if (newTunes.isNotEmpty() || newCatalogs.isNotEmpty() || newHymns.isNotEmpty()) {
                val hymnAssetUpdates = hymnAssetUpdates()
                hymnsRepository.updateHymnAsset(hymnAssetUpdates).blockingAwait()
            }
            if (newTunes.isNotEmpty() || newCatalogs.isNotEmpty() || newHymns.isNotEmpty()) {
                updateBundledArchivePreferences()
            }
            emitter.onSuccess(Result.success())
        }

    }

    private fun hymnAssetUpdates(): List<HymnAssetUpdate> {
        val externalFilesDir = context.getExternalFilesDir(null)
        val midiTunesDir =
            File(externalFilesDir, context.getString(R.string.file_path_midi))

        val sheetMusicCatalogDir =
            File(externalFilesDir, context.getString(R.string.file_path_catalogs))


        val midiFiles = midiTunesDir.list { _, name ->
            name.contains("hymn_")
                    && name.endsWith(".mid", true)
        }.orEmpty()
            .mapNotNull { filename ->
                val hymnId = hymnIdFromFilename(filename)
                val fullMidiPath = File(midiTunesDir, filename).absolutePath
                hymnId?.let { it to fullMidiPath }
            }.associate { it }
        val sheetMusicCatalogFiles = sheetMusicCatalogDir.list { _, name ->
            name.contains("hymn_")
                    && name.endsWith(".pdf")
        }.orEmpty()
            .mapNotNull { filename ->
                val hymnId = hymnIdFromFilename(filename)
                val fullSheetMusicPath = File(sheetMusicCatalogDir, filename).absolutePath
                hymnId?.let { it to fullSheetMusicPath }
            }.associate { it }

        val allHymnIndices = hymnsRepository.loadHymnIndices(BY_NUMBER).blockingFirst()
        val hymnAssetUpdates = allHymnIndices.map {
            HymnAssetUpdate(
                num = it.number,
                midi = midiFiles[it.number],
                localUri = sheetMusicCatalogFiles[it.number]
            )
        }
        return hymnAssetUpdates
    }

    private fun hymnIdFromFilename(filename: String) = filename.substring(
        filename.lastIndexOf("_") + 1,
        filename.lastIndexOf(".")
    ).toIntOrNull()

}
