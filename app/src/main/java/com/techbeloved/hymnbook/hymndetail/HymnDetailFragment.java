package com.techbeloved.hymnbook.hymndetail;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.techbeloved.hymnbook.R;

import java.util.Objects;

import static com.techbeloved.hymnbook.data.HymnContract.*;
import static xdroid.toaster.Toaster.toast;

/**
 * Created by kennedy on 5/2/18.
 */

public class HymnDetailFragment extends Fragment {
    public static final String ARG_ID = "hymn_id";
    public static final String ARG_CURR_ID = "current_id";
    // Externally accessible variables
    private String mHymnTitle;
    private String mHymnTopic;
    private long mCurrentHymnId;

    private long mHymnId;

    private WebView mContentView;

    static HymnDetailFragment init(long hymnId) {
        HymnDetailFragment detailFragment = new HymnDetailFragment();
        // Supply hymnId input as an argument
        Bundle args = new Bundle();
        args.putLong(ARG_ID, hymnId);
        detailFragment.setArguments(args);
        return detailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHymnId = getArguments().getLong(ARG_ID);

        // Get the content from the database

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mContentView = rootView.findViewById(R.id.detail_webview);

        Uri hymnUri = ContentUris.withAppendedId(HymnEntry.CONTENT_URI, mHymnId);
        String fullyQualifiedId = HymnEntry.TABLE_NAME + "." + HymnEntry._ID;

        String[] projection = {
                fullyQualifiedId,
                HymnEntry.COLUMN_TITLE,
                TopicEntry.COLUMN_TITLE,
                HymnEntry.COLUMN_CONTENT,
                HymnEntry.COLUMN_LYRICS,
                HymnEntry.COLUMN_MUSIC,
                HymnEntry.COLUMN_CREDITS
        };
        Cursor cursor = getActivity().getContentResolver().query(hymnUri,
                projection,
                null,
                null,
                null
        );

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String title = cursor.getString(cursor.getColumnIndexOrThrow(HymnEntry.COLUMN_TITLE));
            String content = cursor.getString(cursor.getColumnIndexOrThrow(HymnEntry.COLUMN_CONTENT));
            String topic = cursor.getString(cursor.getColumnIndexOrThrow(TopicEntry.COLUMN_TITLE));

            String lyricsBy = cursor.getString(cursor.getColumnIndexOrThrow(HymnEntry.COLUMN_LYRICS));
            String musicBy = cursor.getString(cursor.getColumnIndexOrThrow(HymnEntry.COLUMN_MUSIC));
            String credits = cursor.getString(cursor.getColumnIndexOrThrow(HymnEntry.COLUMN_CREDITS));


            String htmlBodyTemplate = getString(R.string.html_body_template);
            // The html skeletons for the footer
            String lyricsByFormat = "<p><span class=\"footer-header\">Lyrics by: </span> <span class=\"lyrics\">%1$s</span> </p>";
            String musicByFormat = "<p><span class=\"footer-header\">Music by: </span><span class=\"music\">%1$s</span></p>";
            String creditsFormat = "<p><span class=\"footer-header\">Credits: </span><span class=\"credits\">%1$s</span></p>";

            // Build the footer
            StringBuilder footerBuilder = new StringBuilder();
            footerBuilder.append("<footer>");
            boolean hasFooter = false;
            if (lyricsBy != null && !lyricsBy.isEmpty()) {
                footerBuilder.append(String.format(lyricsByFormat, lyricsBy));
                hasFooter = true;
            }
            if (musicBy != null && !musicBy.isEmpty()) {
                footerBuilder.append(String.format(musicByFormat, musicBy));
                hasFooter = true;
            }
            if (credits != null && !credits.isEmpty()) {
                footerBuilder.append(String.format(creditsFormat, credits));
                hasFooter = true;
            }
            footerBuilder.append("</footer>");
            String footer = footerBuilder.toString();

            // Insert the hymn number, title and content in the html template. lol
            String webData = String.format(htmlBodyTemplate, mHymnId, title, content);

            // Append the footer
            if (hasFooter) {
                webData = webData + footer;
            }

            // Append stylesheet link
            String css_link = getString(R.string.css_link);
            webData = css_link + webData;

            mContentView.loadDataWithBaseURL("file:///android_asset/",
                    webData, "text/html", "UTF-8", null);

            // increase the text size for tablet
            if (getContext().getResources().getBoolean(R.bool.isTablet)) {
                mContentView.getSettings().setTextZoom(120);
            }
            // Set the title of the tool bar
            mHymnTitle = mHymnId + ". " + title;
            mHymnTopic = topic;
            mCurrentHymnId = mHymnId;
            cursor.close();

        }
        return rootView;
    }

    public long getCurrentHymnId() {
        return mCurrentHymnId;
    }

    public String getHymnTitle() {
        return mHymnTitle;
    }

    public String getHymnTopic() {
        return mHymnTopic;
    }
}
