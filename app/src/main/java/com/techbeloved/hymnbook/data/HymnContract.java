package com.techbeloved.hymnbook.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by kennedy on 4/28/18.
 * {@link HymnContract} Contract for the hymn app
 */

public final class HymnContract {

    public static final String CONTENT_AUTHORITY = "com.techbeloved.hymbook";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_HYMNS = "hymns";
    public static final String PATH_TOPICS = "topics";

    private HymnContract() {
    }

    public static final class HymnEntry implements BaseColumns {
        public static final String TABLE_NAME = "hymns";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_AUTHOR_INFO = "author_info";
        public static final String COLUMN_TOPIC_ID = "topic_id";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_HYMNS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of hymns
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_HYMNS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_HYMNS;
    }

    public static final class TopicEntry implements BaseColumns {
        public static final String TABLE_NAME = "topics";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_TITLE = "topic";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TOPICS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of hymns
         */
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_TOPICS;
        /**
         * The MIME type of the {@link #CONTENT_URI} for a single pet.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TOPICS;
    }
}
