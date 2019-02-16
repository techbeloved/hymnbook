package com.techbeloved.hymnbook.data.repo.local;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.techbeloved.hymnbook.data.repo.local.util.AppExecutors;

import java.lang.reflect.Type;
import java.util.List;

import androidx.room.Room;
import androidx.room.TypeConverter;

public class ListConverter {
    @TypeConverter
    public static List<String> toStringList(String jsonString) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(jsonString, type);
    }

    @TypeConverter
    public static String toJSonString(List<String> genreList) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        return gson.toJson(genreList, type);
    }
}
