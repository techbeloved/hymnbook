package com.techbeloved.hymnbook.data.repo.local.util

import com.techbeloved.hymnbook.HymnbookApp
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.Topic
import com.techbeloved.hymnbook.di.AppModule
import kotlinx.serialization.builtins.ListSerializer
import timber.log.Timber
import java.io.IOException
import java.nio.charset.Charset

object DataGenerator {

    fun generateHymns(): List<Hymn> {

        val json = AppModule.provideJson()
        return getHymnFiles().map { filename -> loadHymnJsonFromAsset(filename).also { Timber.d("Loaded $filename") } }
            .flatMap { hymnJson ->
                hymnJson?.let { json.decodeFromString(ListSerializer(Hymn.serializer()), it) }
                    ?: emptyList()
            }
    }

    fun generateTopics(): List<Topic> {
        val json = AppModule.provideJson()

        return getTopicFiles().map { filename -> loadTopicsJsonFromAsset(filename) }
            .flatMap { topicJson ->
                topicJson?.let { json.decodeFromString(ListSerializer(Topic.serializer()), it) }
                    ?: emptyList()
            }
    }

    private fun loadHymnJsonFromAsset(filename: String): String? {
        val json: String?
        try {
            val inputStream = HymnbookApp.instance.assets.open(filename)
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

    /**
     * Preloaded hymn files must be json and contain the word "hymns" and located in the assets folder
     */
    private fun getHymnFiles(): List<String> {
        return getAssetFiles(".*hymns.*\\.json$".toRegex())
    }

    private fun getTopicFiles(): List<String> {
        return getAssetFiles(".*topics.*\\.json$".toRegex())
    }

    private fun getAssetFiles(pattern: Regex) =
        (HymnbookApp.instance.assets.list("")?.filter { it.matches(pattern) }
            ?: emptyList())

    fun loadTopicsJsonFromAsset(filename: String): String? {
        val json: String?
        try {
            val inputStream = HymnbookApp.instance.assets.open(filename)
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
