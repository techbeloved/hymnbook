package com.techbeloved.hymnbook;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import android.widget.AdapterView;
import android.widget.ListView;

import static com.techbeloved.hymnbook.data.HymnContract.*;

/**
 * Created by kennedy on 4/6/18.
 */

public class HymnTitlesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = HymnTitlesFragment.class.getSimpleName();

    private static final int LOADER_ID = 1;
    Parcelable state;
    private HymnCursorAdapter mCursorAdapter;
    private ListView mHymnListView;

    public HymnTitlesFragment() {
        // Empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);

        mHymnListView = rootView.findViewById(R.id.hymn_listview);
        mCursorAdapter = new HymnCursorAdapter(getActivity(), null, false);
        mHymnListView.setAdapter(mCursorAdapter);

        // Enable Toolbar scrolling with listview
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mHymnListView.setNestedScrollingEnabled(true);
        }

        // Set up click listener
        mHymnListView.setOnItemClickListener((parent, view, position, id) -> {
            Uri uri = Uri.withAppendedPath(HymnEntry.CONTENT_URI, String.valueOf(id));
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    uri,
                    parent.getContext(),
                    HymnDetailActivity.class
            );
            startActivity(intent);
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);
        return rootView;
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause: saving HymnListView State");
        state = mHymnListView.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (state != null) {
            Log.d(TAG, "onViewCreated: restoring listview state");
            mHymnListView.onRestoreInstanceState(state);
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        // Define a projection that specifies the columns you want to retrieve
        String[] projection = {HymnEntry._ID, HymnEntry.COLUMN_TITLE};

        switch (id) {
            case LOADER_ID:
                return new CursorLoader(getActivity(),
                        HymnEntry.CONTENT_URI,
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
