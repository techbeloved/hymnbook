package com.techbeloved.hymnbook.usecases

import android.content.Context
import com.techbeloved.hymnbook.data.model.Topic
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.io.IOException
import java.nio.charset.Charset
import javax.inject.Inject

class GetBundledTopicsAssetsUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json,
) {
    operator fun invoke(): List<Topic> = context.assets.list("")
        ?.filter { it.matches(".*topics.*\\.json$".toRegex()) }
        ?.mapNotNull(::loadTopicsJsonFromAsset)
        ?.flatMap { json.decodeFromString(ListSerializer(Topic.serializer()), it) }
        ?: emptyList()

    private fun loadTopicsJsonFromAsset(filename: String): String? {
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
