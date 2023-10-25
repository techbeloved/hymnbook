package com.techbeloved.hymnbook.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetBundledCatalogAssetsUseCase @Inject constructor(@ApplicationContext private val context: Context) {
    operator fun invoke(): List<String> = context.assets.list("sheets")
        ?.filter { it.endsWith(".zip") }
        ?.map { "sheets/$it" }
        ?: emptyList()
}
