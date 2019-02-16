package com.techbeloved.hymnbook.hymns;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.appcompat.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.techbeloved.hymnbook.R;
import com.techbeloved.hymnbook.data.HymnContract;
import com.techbeloved.hymnbook.hymndetail.HymnDetailActivity;
import com.techbeloved.hymnbook.utils.FavoritePreferences;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    HymnCursorAdapter mCursorAdapter;
    ListView mFavoriteListView;

    FavoritePreferences favoritePreferences;
    ActionMode mActionMode;

    public FavoritesFragment() {
    }

    /**
     * Contextual action mode callback
     */
    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
//                    deleteCurrentItem();
                    mode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

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

    // Handles selecting of list item; creating context menu for deleting selected items
    private AbsListView.MultiChoiceModeListener multiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {
        ArrayList<Long> selectedItems = new ArrayList<>();

        @Override
        public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int position, long id, boolean checked) {
            if (checked) {
                selectedItems.add(id);
            } else if (selectedItems.contains(id)) {
                selectedItems.remove(id);
            }
            actionMode.setTitle(String.valueOf(selectedItems.size()));
        }

        @Override
        public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
            // Inflate a menu resource providing context menu items
            MenuInflater inflater = actionMode.getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.delete:
                    deleteSelectedItems(selectedItems);
                    actionMode.finish();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode actionMode) {
            // Update the view
            if (favoritePreferences.getFavorites(getActivity()) != null &&
                    favoritePreferences.getFavorites(getActivity()).size() > 0) {
                getLoaderManager().restartLoader(LOADER_ID, null, FavoritesFragment.this);
            } else mCursorAdapter.swapCursor(null);
            selectedItems.clear();
        }
    };

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
        mFavoriteListView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        mFavoriteListView.setMultiChoiceModeListener(multiChoiceModeListener);

        ImageView emptyImage = rootView.findViewById(R.id.empty_search_image);
        TextView emptyTitle = rootView.findViewById(R.id.empty_title_text);
        TextView emptySubtTitle = rootView.findViewById(R.id.empty_subtitle_text);

        emptyImage.setImageResource(R.drawable.im_favorite_border_black_24dp);
        emptyImage.setImageAlpha(50);
        emptyTitle.setText(R.string.no_favorites_found);
        emptySubtTitle.setText(R.string.add_favorite_hint);

        // Favorites should be reloaded every time to make sure newly added songs are retrieved
        // Only do this if there is at least a favorite in the first place
        if (favoritePreferences.getFavorites(getActivity()) != null &&
                favoritePreferences.getFavorites(getActivity()).size() > 0) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        }
        return rootView;
    }

    /**
     * Takes a list of items to remove and removes them from the preferences
     *
     * @param selectedItems is a list of selected items you want to remove
     */
    private void deleteSelectedItems(ArrayList<Long> selectedItems) {
        for (long id :
                selectedItems) {
            favoritePreferences.removeFavorite(getActivity(), id);
        }
    }

}
