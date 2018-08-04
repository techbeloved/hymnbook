package com.techbeloved.hymnbook.hymns;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.techbeloved.hymnbook.R;
import com.techbeloved.hymnbook.data.HymnContract;

import static com.techbeloved.hymnbook.data.HymnContract.*;

/**
 * Created by kennedy on 4/6/18.
 */

public class TopicsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    Parcelable state;
    private TopicCursorAdapter mCursorAdapter;
    private ListView mTopicListView;

    public TopicsFragment() {
        // Empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);

        mTopicListView = rootView.findViewById(R.id.list);
        mCursorAdapter = new TopicCursorAdapter(getActivity(), null);
        mTopicListView.setAdapter(mCursorAdapter);

        // Enable Toolbar scrolling with listview
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTopicListView.setNestedScrollingEnabled(true);
        }

        // Set up click listener
        mTopicListView.setOnItemClickListener((parent, view, position, id) -> {
            // Get the present title
            Cursor cursor = (Cursor) mCursorAdapter.getItem(position);
            String hymnTitle = cursor.getString(cursor.getColumnIndexOrThrow(HymnContract.TopicEntry.COLUMN_TITLE));
            Intent intent = new Intent(getContext(), TopicHymnListActivity.class);
            intent.putExtra(TopicHymnListActivity.TOPIC_ID, id);
            intent.putExtra(TopicHymnListActivity.TOPIC_NAME, hymnTitle);
            startActivity(intent);
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public void onPause() {
        state = mTopicListView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (state != null) {
            mTopicListView.onRestoreInstanceState(state);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Define a projection that specifies the columns you want to retrieve
        String[] projection = {TopicEntry._ID, TopicEntry.COLUMN_TITLE};

        switch (id) {
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        TopicEntry.CONTENT_URI,
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
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mCursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

}
