package com.techbeloved.hymnbook;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.techbeloved.hymnbook.data.HymnContract;
import com.techbeloved.hymnbook.utils.FavoritePreferences;

import java.util.ArrayList;
import java.util.Arrays;

public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private static final String TAG = FavoritesFragment.class.getSimpleName();
    HymnCursorAdapter mCursorAdapter;
    ListView mFavoriteListView;

    FavoritePreferences favoritePreferences;

    public FavoritesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_song_list, container, false);

        mFavoriteListView = rootView.findViewById(R.id.list);
        mCursorAdapter = new HymnCursorAdapter(getActivity(), null, false);
        mFavoriteListView.setAdapter(mCursorAdapter);

        favoritePreferences = new FavoritePreferences();

        // Enable Toolbar scrolling with listview
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mFavoriteListView.setNestedScrollingEnabled(true);
        }

        View emptyView = rootView.findViewById(R.id.empty);
        mFavoriteListView.setEmptyView(emptyView);
        mFavoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = Uri.withAppendedPath(HymnContract.HymnEntry.CONTENT_URI, String.valueOf(id));
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        uri,
                        parent.getContext(),
                        HymnDetailActivity.class
                );
                startActivity(intent);
            }
        });

        ImageView emptyImage = rootView.findViewById(R.id.empty_search_image);
        TextView emptyTitle = rootView.findViewById(R.id.empty_title_text);
        TextView emptySubtTitle = rootView.findViewById(R.id.empty_subtitle_text);

        emptyImage.setImageResource(R.drawable.watchman_logo);
        emptyTitle.setText(R.string.no_favorites_found);
        emptySubtTitle.setText(R.string.add_favorite_hint);

        // Favorites should be reloaded every time to make sure newly added songs are retrieved
        // Only do this if there is at least a favorite in the first place
        if (favoritePreferences.getFavorites(getActivity()) != null) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
        return rootView;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case LOADER_ID:
                ArrayList<Long> prefs = favoritePreferences.getFavorites(getActivity());
                boolean first = true;
                if (prefs != null) {
                    String[] projection = {HymnContract.HymnEntry._ID, HymnContract.HymnEntry.COLUMN_TITLE};
                    // Seriously, had a hard time figuring out this solution. Happens that you can't supply selectionArgs
                    // values containing brackets
                    // https://stackoverflow.com/questions/23610036/androidsqlite-querying-an-array-in-selectionargs-as-well-as-other-string-valu
                    String selection = HymnContract.HymnEntry._ID + " IN (" + makePlaceholders(prefs.size()) + ")";
                    String[] selectArgs = new String[prefs.size()];
                    for (int i = 0; i < prefs.size(); i++) {
                        selectArgs[i] = String.valueOf(prefs.get(i));
                    }

                    return new CursorLoader(
                            getActivity(),
                            HymnContract.HymnEntry.CONTENT_URI,
                            projection,
                            selection,
                            selectArgs,
                            null
                    );
                }
                return null;
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }

    /**
     * Generates given number of placeholders for the sql query. That is, using ?
     *
     * @param len is the number of placeholders to generate
     * @return a string replresentation of the place holders, eg. "?,?,?,?" for len = 4
     */
    String makePlaceholders(int len) {
        StringBuilder sb = new StringBuilder(len * 2 - 1);
        sb.append("?");
        for (int i = 1; i < len; i++)
            sb.append(",?");
        return sb.toString();
    }

}
