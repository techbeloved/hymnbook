package com.techbeloved.hymnbook.utils.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.techbeloved.hymnbook.R
import com.techbeloved.hymnbook.usecases.GetBundledCatalogAssetsUseCase
import com.techbeloved.hymnbook.usecases.GetBundledTunesAssetsUseCase
import com.techbeloved.hymnbook.usecases.GetCatalogArchivePreferenceUseCase
import com.techbeloved.hymnbook.usecases.GetMidiArchivePreferenceUseCase
import com.techbeloved.hymnbook.utils.Decompress
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single
import timber.log.Timber
import java.io.File
import java.util.zip.ZipInputStream

@HiltWorker
class ExtractBundledAssetsWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val decompress: Decompress,
    private val getBundledCatalogAssetsUseCase: GetBundledCatalogAssetsUseCase,
    private val getBundledTunesAssetsUseCase: GetBundledTunesAssetsUseCase,
    private val getMidiArchivePreferenceUseCase: GetMidiArchivePreferenceUseCase,
    private val getCatalogArchivePreferenceUseCase: GetCatalogArchivePreferenceUseCase,
) : RxWorker(context, params) {
    override fun createWork(): Single<Result> = Single.create { emitter ->

        val bundledTunes = getBundledTunesAssetsUseCase()
        val bundledCatalogs = getBundledCatalogAssetsUseCase()
        val alreadySavedTunes = getMidiArchivePreferenceUseCase()
        val alreadySavedCatalogs = getCatalogArchivePreferenceUseCase()

        val newTunes = bundledTunes.filter { !alreadySavedTunes.contains(it) }
        val newCatalogs = bundledCatalogs.filter { !alreadySavedCatalogs.contains(it) }


        val externalFilesDir = context.getExternalFilesDir(null)
        val midiTunesDir =
            File(externalFilesDir, context.getString(R.string.file_path_midi))
                .also { it.mkdirs() }

        val sheetMusicCatalogDir =
            File(externalFilesDir, context.getString(R.string.file_path_catalogs))
                .also { it.mkdirs() }

        val result = try {
            for (tuneZip in newTunes) {
                context.assets.open(tuneZip).use { assetInput ->
                    decompress.unzip(ZipInputStream(assetInput), midiTunesDir)
                }
            }
            for (sheetZip in newCatalogs) {
                context.assets.open(sheetZip).use { assetInput ->
                    decompress.unzip(ZipInputStream(assetInput), sheetMusicCatalogDir)
                }
            }
            Result.success()
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure()
        }
        emitter.onSuccess(result)
    }
}
