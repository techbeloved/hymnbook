package com.techbeloved.hymnbook.usecases

import android.content.Context
import com.techbeloved.hymnbook.data.model.Hymn
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.IOException
import java.nio.charset.Charset
import javax.inject.Inject

class GetHymnsFromJsonAssetUseCase @Inject constructor(
    private val json: Json,
    @ApplicationContext private val context: Context,
) {

    operator fun invoke(filename: String): List<Hymn> = loadHymnJsonFromAsset(filename)?.let {
        json.decodeFromString(ListSerializer(Hymn.serializer()), it)
    } ?: emptyList()

    private fun loadHymnJsonFromAsset(filename: String): String? {
        val json: String?
        try {
            val inputStream = context.assets.open(filename)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }
}
