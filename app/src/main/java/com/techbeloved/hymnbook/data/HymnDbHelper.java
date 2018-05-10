package com.techbeloved.hymnbook.data;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by kennedy on 4/28/18.
 */

public class HymnDbHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "hymnbook_v2.db";
    private static final int DATABASE_VERSION = 2;

    public HymnDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
