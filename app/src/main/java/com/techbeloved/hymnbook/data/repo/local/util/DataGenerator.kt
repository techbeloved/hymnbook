package com.techbeloved.hymnbook.data.repo.local.util

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.techbeloved.hymnbook.HymnbookApp
import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.Topic
import java.io.IOException
import java.nio.charset.Charset

object DataGenerator {

    fun generateHymns(): List<Hymn> {
        val typeOfHymnList = object : TypeToken<List<Hymn>>() {}.type

        return GsonBuilder().create().fromJson(loadHymnJsonFromAsset(), typeOfHymnList)
    }

    fun generateTopics(): List<Topic> {
        val typeOfTopicList = object : TypeToken<List<Topic>>() {}.type
        val topics = GsonBuilder().create().fromJson<List<Topic>>(loadTopicsJsonFromAsset(), typeOfTopicList)
        return topics
    }

    fun loadHymnJsonFromAsset(): String? {
        val json: String?
        try {
            val inputStream = HymnbookApp.instance.assets.open("all_hymns_v5.json")
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

    fun loadTopicsJsonFromAsset(): String? {
        val json: String?
        try {
            val inputStream = HymnbookApp.instance.assets.open("all_topics.json")
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
