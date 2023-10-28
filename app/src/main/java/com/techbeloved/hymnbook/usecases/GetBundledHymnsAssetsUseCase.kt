package com.techbeloved.hymnbook.usecases

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class GetBundledHymnsAssetsUseCase @Inject constructor(@ApplicationContext private val context: Context) {
    operator fun invoke(): List<String> = context.assets.list("")
        ?.filter { it.matches(".*hymns.*\\.json$".toRegex()) }
        ?: emptyList()
}
