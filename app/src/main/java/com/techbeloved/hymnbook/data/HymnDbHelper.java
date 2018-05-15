package com.techbeloved.hymnbook.data;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by kennedy on 4/28/18.
 */

public class HymnDbHelper extends SQLiteAssetHelper {

    public static final String DATABASE_NAME = "hymnbook.db";
    private static final int DATABASE_VERSION = 3;

    public HymnDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        setForcedUpgrade(DATABASE_VERSION);
    }
}
