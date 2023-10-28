package com.techbeloved.hymnbook.usecases

import javax.inject.Inject

class UpdateBundledArchivePreferences @Inject constructor(
    private val getBundledCatalogAssetsUseCase: GetBundledCatalogAssetsUseCase,
    private val getBundledTunesAssetsUseCase: GetBundledTunesAssetsUseCase,
    private val getBundledHymnsAssetsUseCase: GetBundledHymnsAssetsUseCase,
    private val setCatalogArchivePreferenceUseCase: SetCatalogArchivePreferenceUseCase,
    private val setTunesArchivePreferenceUseCase: SetMidiArchivePreferenceUseCase,
    private val setHymnsArchivePreferenceUseCase: SetHymnsArchivePreferenceUseCase,
) {

    operator fun invoke() {
        val bundledCatalogs = getBundledCatalogAssetsUseCase()
        val bundledTunes = getBundledTunesAssetsUseCase()
        val bundledHymns = getBundledHymnsAssetsUseCase()

        setCatalogArchivePreferenceUseCase(bundledCatalogs.toSet())
        setTunesArchivePreferenceUseCase(bundledTunes.toSet())
        setHymnsArchivePreferenceUseCase(bundledHymns.toSet())
    }
}
