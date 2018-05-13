package com.techbeloved.hymnbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.techbeloved.hymnbook.data.HymnContract;

/**
 * Created by kennedy on 5/13/18.
 * Handles the search query through an FTS3 sqlite table
 */

class SearchDatabaseTable {

    static final String COL_ID = "_id";
    static final String COL_TITLE = "title";
    static final String COL_CONTENT = "content";
    private static final String TAG = SearchDatabaseTable.class.getSimpleName();
    private static final String DATABASE_NAME = "HYMNBOOK_SEARCH_DB";
    private static final String FTS_VIRTUAL_TABLE = "FTS";
    private static final int DATABASE_VERSION = 1;

    private final DatabaseOpenHelper mDatabaseOpenHelper;

    SearchDatabaseTable(Context context) {
        mDatabaseOpenHelper = new DatabaseOpenHelper(context);
    }

    Cursor getWordMatches(String query) {
        // Something like select * from docs where docs MATCH ? . this searches all the columns in
        // the table for the given string
        String selection = FTS_VIRTUAL_TABLE + " MATCH ?";
        String[] selectionArgs = new String[]{query};

        return query(selection, selectionArgs);
    }

    private Cursor query(String selection, String[] selectionArgs) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(FTS_VIRTUAL_TABLE);

        SQLiteDatabase db = mDatabaseOpenHelper.getReadableDatabase();
        Log.i(TAG, "query: " + db.toString());

        Cursor cursor = builder.query(db,
                null, selection, selectionArgs, null, null, null);
        Log.i(TAG, "query: result count " + cursor.getCount());
        //cursor.moveToFirst();


        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }
        return cursor;
    }

    private static class DatabaseOpenHelper extends SQLiteOpenHelper {

        private static final String FTS_TABLE_CREATE =
                "CREATE VIRTUAL TABLE " + FTS_VIRTUAL_TABLE +
                        " USING fts3 (" + COL_ID + "," +
                        COL_TITLE + ", " +
                        COL_CONTENT + ")";
        private final Context mHelperContext;
        private SQLiteDatabase mDatabase;

        DatabaseOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            Log.i(TAG, "DatabaseOpenHelper: ");
            mHelperContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            mDatabase = db;
            mDatabase.execSQL(FTS_TABLE_CREATE);
            Log.i(TAG, "onCreate: about to search");
            loadSearchTable();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + FTS_VIRTUAL_TABLE);
            onCreate(db);
        }

        private void loadSearchTable() {
            Log.i(TAG, "loadSearchTable: loading the search table");
            new Thread(this::loadSearchData).start();
        }

        private void loadSearchData() {
            final Context context = mHelperContext.getApplicationContext();
            Cursor cursor;

            String[] projection = {
                    HymnContract.HymnEntry._ID,
                    HymnContract.HymnEntry.COLUMN_TITLE,
                    HymnContract.HymnEntry.COLUMN_CONTENT
            };
            cursor = context.getContentResolver().query(HymnContract.HymnEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null
            );
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    int index = cursor.getInt(cursor.getColumnIndex(COL_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(HymnContract.HymnEntry.COLUMN_TITLE));
                    String content = cursor.getString(cursor.getColumnIndexOrThrow(HymnContract.HymnEntry.COLUMN_CONTENT));

                    String firstVerseAndChorus;
                    if (content != null) {
                        String[] contentSplit = content.split("</li>");
                        firstVerseAndChorus = contentSplit[0]
                                .replaceAll("<span>", "")
                                .replaceAll("</span>", "\n")
                                .replaceAll("<ol>", "")
                                .replaceAll("<li>", "")
                                .replaceAll("<p class=\"chorus\">", "")
                                .replaceAll("</p>", "");
                        Log.i(TAG, "loadSearchData: " + firstVerseAndChorus);
                    } else {
                        firstVerseAndChorus = null;
                    }

                    long status = addData(index, title, firstVerseAndChorus);
                    if (status < 0) {
                        Log.e(TAG, "loadSearchData: unable to add data: " + title);
                    }
                    cursor.moveToNext();
                }
                cursor.close();

            }
        }

        long addData(int index, String title, String content) {
            ContentValues initialValues = new ContentValues();
            initialValues.put(COL_ID, index);
            initialValues.put(COL_TITLE, title);
            initialValues.put(COL_CONTENT, content);
            return mDatabase.insert(FTS_VIRTUAL_TABLE, null, initialValues);
        }
    }
}
