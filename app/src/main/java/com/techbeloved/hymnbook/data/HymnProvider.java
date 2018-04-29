package com.techbeloved.hymnbook.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by kennedy on 4/28/18
 * {@link ContentProvider} for  the hymnbook app.
 */

public class HymnProvider extends ContentProvider {

    public static final String LOG_TAG = HymnProvider.class.getSimpleName();

    // Constants for use by the url matcher: uses them to identify the different query uris
    private static final int HYMNS = 100;
    private static final int HYMN_ID = 101;
    private static final int TOPICS = 102;
    private static final int TOPIC_ID = 103;
    private static final int TOPIC_HYMNS = 104;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(HymnContract.CONTENT_AUTHORITY, HymnContract.PATH_HYMNS, HYMNS);
        sUriMatcher.addURI(HymnContract.CONTENT_AUTHORITY, HymnContract.PATH_HYMNS + "/#", HYMN_ID);
        sUriMatcher.addURI(HymnContract.CONTENT_AUTHORITY, HymnContract.PATH_TOPICS, TOPICS);
        sUriMatcher.addURI(HymnContract.CONTENT_AUTHORITY, HymnContract.PATH_TOPICS + "/#", TOPIC_ID);
        // For hymns under a particular topic
        sUriMatcher.addURI(HymnContract.CONTENT_AUTHORITY, HymnContract.PATH_TOPICS + "/#" + "/hymns", TOPIC_HYMNS);
    }

    /**
     * Initialize the provider and the database helper object
     */
    private HymnDbHelper mDbHelper;

    @Override
    public boolean onCreate() {

        mDbHelper = new HymnDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);

        switch (match) {
            case HYMNS:
                cursor = db.query(HymnContract.HymnEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder
                );
                break;

            case HYMN_ID:
                selection = HymnContract.HymnEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(HymnContract.HymnEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case TOPICS:
                cursor = db.query(HymnContract.TopicEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder
                );
                break;

            case TOPIC_ID:
                selection = HymnContract.TopicEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(HymnContract.TopicEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            case TOPIC_HYMNS:
                // This catches for example, "content://com.techbeloved.hymnbook/topics/3/hymns"
                // Which implies getting all hymns under topic 3
                // So we use a simple reference to get all hymns whose topic id matches that
                // given in the uri
                selection = HymnContract.HymnEntry.COLUMN_TOPIC_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = db.query(HymnContract.HymnEntry.TABLE_NAME,
                        projection, selection, selectionArgs,
                        null, null, sortOrder
                );
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        return cursor;
    }

    /**
     * Gets the MIME type of data for the content URI
     *
     * @param uri the uri we want to query with
     * @return MIME type
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case HYMNS:
            case TOPIC_HYMNS:
                return HymnContract.HymnEntry.CONTENT_LIST_TYPE;
            case TOPICS:
                return HymnContract.TopicEntry.CONTENT_LIST_TYPE;
            case HYMN_ID:
                return HymnContract.HymnEntry.CONTENT_ITEM_TYPE;
            case TOPIC_ID:
                return HymnContract.TopicEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    // our database is ready made, so we do not have to update
    // Or insert anything
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
