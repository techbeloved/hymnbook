package com.techbeloved.hymnbook;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import static com.techbeloved.hymnbook.data.HymnContract.HymnEntry;

public class HymnDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = HymnDetailActivity.class.getSimpleName();
    private static final int LOADER_ID = 1;
    public static String hymn_tag = "hymn_number";
    private Uri mUri;
    private TextView mToolbarTitle;
    private TextView mToolbarTopic;

    private long mHymnId;

    private ViewPager mPager;
    private CursorPagerAdapter mAdapter;

    private ActionBar mActionBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hymn_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default title
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mActionBar = getSupportActionBar();
//        mToolbarTitle = findViewById(R.id.toolbar_title);
//        mToolbarTopic = findViewById(R.id.toolbar_topic);

        mPager = findViewById(R.id.detail_pager);
        mAdapter = new CursorPagerAdapter(getSupportFragmentManager(), null);
        mPager.setAdapter(mAdapter);

        // Get uri sent by hymn list
        mUri = getIntent().getData();
        mHymnId = ContentUris.parseId(mUri);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putLong(HymnDetailFragment.ARG_CURR_ID, mAdapter.getCurrentFragment().currentHymnId);
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState: " + mAdapter.getCurrentFragment().currentHymnId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the state, that is, the current adapter position
        mHymnId = savedInstanceState.getLong(HymnDetailFragment.ARG_CURR_ID);
        mPager.setCurrentItem((int) mHymnId - 1, true);

        Log.i(TAG, "onRestoreInstanceState: hymn_id: " + mHymnId);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this, HymnEntry.CONTENT_URI,
                        new String[]{HymnEntry._ID},
                        null,
                        null,
                        null
                );
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() >= 1) {
            mAdapter.swapCursor(data);
            mPager.setCurrentItem((int) mHymnId - 1);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private class CursorPagerAdapter extends FragmentStatePagerAdapter {

        private Cursor mCursor;
        private HymnDetailFragment mCurrentFragment;

        public CursorPagerAdapter(FragmentManager fm, Cursor c) {
            super(fm);
            mCursor = c;
        }

        @Override
        public Fragment getItem(int position) {
            if (mCursor.moveToPosition(position)) {
                Log.i(TAG, "getItem: " + position);
                long hymnId = mCursor.getLong(mCursor.getColumnIndexOrThrow(HymnEntry._ID));
                return HymnDetailFragment.init(hymnId);
            }
            Log.i(TAG, "getItem: is " + mCursor);
            return null;
        }

        @Override
        public int getCount() {
            if (mCursor != null && mCursor.getCount() > 0) {
                return mCursor.getCount();
            }
            return 0;
        }

        void swapCursor(Cursor cursor) {
            mCursor = cursor;
            notifyDataSetChanged();
        }

        /**
         * Sets the Toolbar title to that of the currently showing fragment
         *
         * @param container The view Group
         * @param position  The current position of the adapter
         * @param object    The current fragment
         */
        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentFragment = (HymnDetailFragment) object;
            // Set the Tool Bar title which is stored in the current instance
            mActionBar.setTitle(mCurrentFragment.hymnTitle);
            mActionBar.setSubtitle(mCurrentFragment.hymnTopic);
        }

        HymnDetailFragment getCurrentFragment() {
            return mCurrentFragment;
        }
    }
}
