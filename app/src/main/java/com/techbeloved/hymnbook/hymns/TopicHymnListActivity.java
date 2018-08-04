package com.techbeloved.hymnbook.hymns;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import com.techbeloved.hymnbook.R;
import com.techbeloved.hymnbook.data.HymnContract;
import com.techbeloved.hymnbook.hymndetail.HymnDetailActivity;

/**
 * Created by kennedy on 5/11/18.
 */

public class TopicHymnListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TOPIC_ID = "topicId";
    public static final String TOPIC_NAME = "topicName";
    private static final int LOADER_ID = 1;
    private ListView mHymnListView;
    private HymnCursorAdapter mAdapter;
    private long mTopicId;
    private String mTopicTitle;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_hymn_list);
        mHymnListView = findViewById(R.id.list);
        mAdapter = new HymnCursorAdapter(this, null, false);
        // Configure the ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mHymnListView.setAdapter(mAdapter);

        Intent initIntent = getIntent();
        // Get the topic id
        mTopicId = initIntent.getLongExtra(TOPIC_ID, 1);
        // Get the topic
        mTopicTitle = initIntent.getStringExtra(TOPIC_NAME);
        // Set the toolbar title
        setTitle(mTopicTitle);

        // Set up click listener
        mHymnListView.setOnItemClickListener((parent, view, position, id) -> {
            Uri uri = Uri.withAppendedPath(HymnContract.HymnEntry.CONTENT_URI, String.valueOf(id));
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    uri,
                    parent.getContext(),
                    HymnDetailActivity.class
            );
            startActivity(intent);
        });

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If UP button is pressed, return to the previous activity
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        // Define a projection that specifies the columns you want to retrieve
        String[] projection = {HymnContract.HymnEntry._ID, HymnContract.HymnEntry.COLUMN_TITLE};

        Uri uri = ContentUris.withAppendedId(HymnContract.TopicEntry.CONTENT_URI, mTopicId);
        uri = Uri.withAppendedPath(uri, "hymns");
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this,
                        uri,
                        projection,
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader loader) {
        mAdapter.swapCursor(null);
    }
}
