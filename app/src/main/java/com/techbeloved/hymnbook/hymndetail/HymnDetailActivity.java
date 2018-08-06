package com.techbeloved.hymnbook.hymndetail;

import android.app.SearchManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.techbeloved.hymnbook.settings.SettingsActivity;
import com.techbeloved.hymnbook.utils.DepthPageTransformer;
import com.techbeloved.hymnbook.R;
import com.techbeloved.hymnbook.utils.FavoritePreferences;
import com.techbeloved.hymnbook.viewgroup.TouchableFrameWrapper;

import java.io.File;
import java.util.Objects;

import static com.techbeloved.hymnbook.data.HymnContract.HymnEntry;

public class HymnDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    private static final String PLAYBACK_POSITION = "playbackPosition";
    // Favorite Preference
    FavoritePreferences favoritePreferences;
    private Uri mUri;
    private FloatingActionButton playFAB;
    // This stores the hymn number, received from an intent and also saved onSaveInstanceState.
    private long mHymnId;
    private ViewPager mPager;
    private CursorPagerAdapter mAdapter;
    private ActionBar mActionBar;
    // Media player states
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private int mPlaybackPos;
    // Set up listener to listen for focus change. Only act when something is playing
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                @Override
                public void onAudioFocusChange(int i) {
                    if (mMediaPlayer != null) {
                        switch (i) {
                            case AudioManager.AUDIOFOCUS_GAIN:
                                // May be returning from a paused state, so resume playback
                                mMediaPlayer.setVolume(1.0f, 1.0f);
                                if (!mMediaPlayer.isPlaying()) {
                                    mMediaPlayer.start();
                                }
                                break;

                            case AudioManager.AUDIOFOCUS_LOSS:
                                // Another app has taken over, stop completely and release MediaPlayer
                                mMediaPlayer.stop();
                                releaseMediaPlayer();
                                break;

                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                                // Lost focus for a short time, but we have to stop
                                // playback. We don't release the media player because playback
                                // is likely to resume
                                mMediaPlayer.pause();
                                break;

                            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                                // Lost focus for a short time, we just reduce the volume
                                mMediaPlayer.setVolume(0.3f, 0.3f);
                                break;

                        }
                    }
                }
            };
    ViewPager.SimpleOnPageChangeListener pageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            // Stop the audio and release mediaPlayer when on a new page
            releaseMediaPlayer();
            playFAB.setImageResource(android.R.drawable.ic_media_play);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hymn_detail);

        setupImmersiveMode();

        // Configure the ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mActionBar = getSupportActionBar();

        mPager = findViewById(R.id.detail_pager);
        mAdapter = new CursorPagerAdapter(getSupportFragmentManager(), null);
        mPager.setAdapter(mAdapter);
        mPager.setPageTransformer(true, new DepthPageTransformer());

        // Get uri sent by hymn list
        mUri = getIntent().getData();
        mHymnId = ContentUris.parseId(mUri);
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        // Initialize AudioManager Service
        mAudioManager = (AudioManager) this.getSystemService(AUDIO_SERVICE);

        playFAB = findViewById(R.id.playFAB);
        playFAB.setOnClickListener(view -> {
            if (mMediaPlayer != null) {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                    playFAB.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    mMediaPlayer.start();
                    playFAB.setImageResource(android.R.drawable.ic_media_pause);
                }
            } else {
                if (playAudio(createAudioUri())) {
                    playFAB.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            // Update the UI to show that mediaPlayer is playing
            playFAB.setImageResource(android.R.drawable.ic_media_pause);
        }

        playFAB.setOnLongClickListener(view -> {
            if (mMediaPlayer != null) {
                releaseMediaPlayer();
                playFAB.setImageResource(android.R.drawable.ic_media_play);
            }
            return true;
        });

        mPager.addOnPageChangeListener(pageChangeListener);

        // Setup favorites
        favoritePreferences = new FavoritePreferences();
    }

    private void setupImmersiveMode() {
        // Setup the gesture listener to listen for single tap and toggle fullscreen
        TouchableFrameWrapper mainView = findViewById(R.id.touchable_frame);
        GestureDetector.SimpleOnGestureListener gestureListener = new GestureListener(() -> {
            toggleHideyBar();
            return false;
        });
        final GestureDetector gd = new GestureDetector(this, gestureListener);
        mainView.setGestureDetector(gd);
    }

    protected void onSaveInstanceState(Bundle outState) {
        mHymnId = mAdapter.getCurrentFragment().getCurrentHymnId();
        if (mMediaPlayer != null) {
            mPlaybackPos = mMediaPlayer.getCurrentPosition();
            outState.putInt(PLAYBACK_POSITION, mPlaybackPos);
        }
        outState.putLong(HymnDetailFragment.ARG_CURR_ID, mHymnId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore the state, that is, the current adapter position
        mHymnId = savedInstanceState.getLong(HymnDetailFragment.ARG_CURR_ID);
        mPager.setCurrentItem((int) mHymnId - 1, true);
        mPlaybackPos = savedInstanceState.getInt(PLAYBACK_POSITION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.details_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        if (searchManager != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName())
            );
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                // Takes care of dismissing the keyboard when search is performed
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        searchView.setIconifiedByDefault(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If UP button is pressed, return to the previous activity
            case android.R.id.home:
                finish();
                return true;
            case R.id.add_favorite:
                addToFavorite();
                return true;
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @NonNull
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

    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;
            mAudioManager.abandonAudioFocus(audioFocusChangeListener);

        }
    }

    /**
     * Handle playing of the audio media, which including requesting for focus and etc
     */
    private boolean playAudio(Uri audioUri) {

        if (audioUri != null) {
            // Request for focus. requestAudioFocus(OnAudioFocusChangeListener l, int streamType, int durationHint)
            int focus = mAudioManager.requestAudioFocus(audioFocusChangeListener,
                    AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
            // Play audio if focus request granted
            if (focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mMediaPlayer = MediaPlayer.create(this, audioUri);
                mMediaPlayer.start();
                mMediaPlayer.setLooping(true);
                return true;
            }
            return false;
        } else {
            return false;
        }
    }

    /**
     * Generates the absolute path for the audio specified by the current hymn number
     *
     * @return the absolute path fo the requested audio
     */
    public Uri createAudioUri() {

        Uri audioUri1 = null;
        int currentNum = (int) mAdapter.getCurrentFragment().getCurrentHymnId();
        final String audioFileTitle = "hymn_" + currentNum + ".mid"; //Hymn is stored as "hymn_1.mid"
        final String privateStoragePath = ContextCompat
                .getExternalFilesDirs(this, null)[0].getAbsolutePath();
        File audioFile = new File(privateStoragePath + "/midi/" + audioFileTitle);
        if (audioFile.exists()) {
            audioUri1 = Uri.parse(audioFile.getAbsolutePath());

        } else { //Download the audio file from internet if not exist locally
            // This is not yet implemented
            Toast.makeText(this, "The tune is " +
                            "not yet available! "
                    , Toast.LENGTH_SHORT).show();
        }

        return audioUri1;

    }

    private void addToFavorite() {
        favoritePreferences.addFavorite(getApplicationContext(), mAdapter.getCurrentFragment().getCurrentHymnId());
        Toast.makeText(this, "Song has been added to favorites", Toast.LENGTH_SHORT).show();
    }
    /**
     * The {@link CursorPagerAdapter }
     */
    private class CursorPagerAdapter extends FragmentStatePagerAdapter {

        private Cursor mCursor;
        private HymnDetailFragment mCurrentFragment;

        CursorPagerAdapter(FragmentManager fm, Cursor c) {
            super(fm);
            mCursor = c;
        }

        @Override
        public Fragment getItem(int position) {
            if (mCursor.moveToPosition(position)) {
                long hymnId = mCursor.getLong(mCursor.getColumnIndexOrThrow(HymnEntry._ID));
                return HymnDetailFragment.init(hymnId);
            }
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
            mActionBar.setTitle(mCurrentFragment.getHymnTitle());
            mActionBar.setSubtitle(mCurrentFragment.getHymnTopic());
        }

        HymnDetailFragment getCurrentFragment() {
            return mCurrentFragment;
        }
    }

    /**
     * Detects and toggles immersive mode (also known as "hidey bar" mode).
     */
    public void toggleHideyBar() {

        // BEGIN_INCLUDE (get_current_ui_flags)
        // The UI options currently enabled are represented by a bitfield.
        // getSystemUiVisibility() gives us that bitfield.
        int newUiOptions = this.getWindow().getDecorView().getSystemUiVisibility();
        // END_INCLUDE (get_current_ui_flags)
        // Hide or show toolbar accordingly
        ActionBar actionBar = getSupportActionBar();
        boolean isImmersiveModeEnabled =
                ((newUiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == newUiOptions);
        if (isImmersiveModeEnabled) {
            if (actionBar != null) {
                actionBar.show();
            }
        } else {
            if (actionBar != null) {
                actionBar.hide();
            }
        }

        // BEGIN_INCLUDE (toggle_ui_flags)
        newUiOptions ^= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        newUiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;


        // Immersive mode: Backward compatible to KitKat.
        // Note that this flag doesn't do anything by itself, it only augments the behavior
        // of HIDE_NAVIGATION and FLAG_FULLSCREEN.  For the purposes of this sample
        // all three flags are being toggled together.
        // Note that there are two immersive mode UI flags, one of which is referred to as "sticky".
        // Sticky immersive mode differs in that it makes the navigation and status bars
        // semi-transparent, and the UI flag does not get cleared when the user interacts with
        // the screen.
        newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        this.getWindow().getDecorView().setSystemUiVisibility(newUiOptions);
        //END_INCLUDE (set_ui_flags)
    }
}
