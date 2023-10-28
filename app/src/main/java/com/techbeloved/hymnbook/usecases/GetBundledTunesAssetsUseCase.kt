package com.techbeloved.hymnbook.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetBundledTunesAssetsUseCase @Inject constructor(@ApplicationContext private val context: Context) {
    operator fun invoke(): List<String> = context.assets.list("tunes")
        ?.filter { it.endsWith(".zip") }
        ?.map { "tunes/$it" }
        ?: emptyList()
}
