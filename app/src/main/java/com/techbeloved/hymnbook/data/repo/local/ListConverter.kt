package com.techbeloved.hymnbook.data.repo.local

import androidx.room.TypeConverter
import com.techbeloved.hymnbook.di.AppModule
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

object ListConverter {
    @JvmStatic
    @TypeConverter
    fun toStringList(jsonString: String?): List<String> {
        val json = AppModule.provideJson()
        if (jsonString.isNullOrBlank()) return emptyList()
        return json.decodeFromString(ListSerializer(String.serializer()), jsonString)
    }

    @JvmStatic
    @TypeConverter
    fun toJSonString(genreList: List<String>): String {
        val json = AppModule.provideJson()
        return json.encodeToString(ListSerializer(String.serializer()), genreList)
    }
}