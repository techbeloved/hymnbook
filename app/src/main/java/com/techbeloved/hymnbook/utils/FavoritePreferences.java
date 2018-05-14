package com.techbeloved.hymnbook.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kennedy on 5/14/18.
 * Handles saving and retrieving of favorites from shared preferences
 */

public class FavoritePreferences {
    public static final String PREFS_NAME = "HYMNS";
    public static final String FAVORITES = "Hymn_Favorite";

    public FavoritePreferences() {
        super();
    }

    // This four methods are used for maintaining favorites.
    private void saveFavorites(Context context, List<Long> favorites) {
        SharedPreferences settings;
        SharedPreferences.Editor editor;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);
        editor = settings.edit();

        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(favorites);

        editor.putString(FAVORITES, jsonFavorites);

        editor.apply();
    }

    public void addFavorite(Context context, long index) {
        List<Long> favorites = getFavorites(context);
        if (favorites == null)
            favorites = new ArrayList<>();
        if (favorites.contains(index)) {
            // no need of adding it
            return;
        }
        favorites.add(index);
        saveFavorites(context, favorites);
    }

    public void removeFavorite(Context context, long index) {
        ArrayList<Long> favorites = getFavorites(context);
        if (favorites != null) {
            favorites.remove(index);
            saveFavorites(context, favorites);
        }
    }

    public ArrayList<Long> getFavorites(Context context) {
        SharedPreferences settings;
        List<Long> favorites;

        settings = context.getSharedPreferences(PREFS_NAME,
                Context.MODE_PRIVATE);

        if (settings.contains(FAVORITES)) {
            String jsonFavorites = settings.getString(FAVORITES, null);
            Gson gson = new Gson();
            Long[] favoriteItems = gson.fromJson(jsonFavorites,
                    Long[].class);

            favorites = Arrays.asList(favoriteItems);
            favorites = new ArrayList<>(favorites);
        } else
            return null;

        return (ArrayList<Long>) favorites;
    }
}