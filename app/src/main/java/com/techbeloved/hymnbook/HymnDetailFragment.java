package com.techbeloved.hymnbook;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import com.techbeloved.hymnbook.data.HymnContract;

import static com.techbeloved.hymnbook.data.HymnContract.*;

/**
 * Created by kennedy on 5/2/18.
 */

public class HymnDetailFragment extends Fragment {
    public static final String ARG_ID = "hymn_id";
    public static final String ARG_CURR_ID = "current_id";
    // Externally accessible variables
    public String hymnTitle;
    public String hymnTopic;
    public long currentHymnId;

    private long mHymnId;

    private WebView mContentView;
    private TextView mToolBarHeader;

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

        String[] projection = {fullyQualifiedId, HymnEntry.COLUMN_TITLE, TopicEntry.COLUMN_TITLE, HymnEntry.COLUMN_CONTENT};
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

            String htmlBodyTemplate = getString(R.string.html_body_template);

            // Insert the hymn number, title and content in the html template. lol
            String webData = String.format(htmlBodyTemplate, mHymnId, title, content);
            String css_link = getString(R.string.css_link);
            webData = css_link + webData;

            mContentView.loadDataWithBaseURL("file:///android_asset/",
                    webData, "text/html", "UTF-8", null);
            // Set the title of the tool bar
            hymnTitle = mHymnId + ". " + title;
            hymnTopic = topic;
            currentHymnId = mHymnId;
            cursor.close();

        }
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }
}
