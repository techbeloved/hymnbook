package com.techbeloved.hymnbook;

import android.content.ContentUris;
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
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import static com.techbeloved.hymnbook.data.HymnContract.HymnEntry;

public class HymnDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = HymnDetailActivity.class.getSimpleName();
    private static final int LOADER_ID = 1;

    private Uri mUri;
    private TextView mToolbarTitle;
    private TextView mToolbarTopic;

    private FloatingActionButton playFAB;

    // This stores the hymn number, received from an intent and also saved onSaveInstanceState.
    private long mHymnId;

    private ViewPager mPager;
    private CursorPagerAdapter mAdapter;

    private ActionBar mActionBar;

    // Media player states
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
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
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hymn_detail);

        // Configure the ToolBar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        playFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });

        playFAB.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (mMediaPlayer != null) {
                    releaseMediaPlayer();
                    playFAB.setImageResource(android.R.drawable.ic_media_play);
                }
                return true;
            }
        });

        mPager.addOnPageChangeListener(pageChangeListener);

    }

    protected void onSaveInstanceState(Bundle outState) {
        mHymnId = mAdapter.getCurrentFragment().getCurrentHymnId();
        outState.putLong(HymnDetailFragment.ARG_CURR_ID, mHymnId);
        super.onSaveInstanceState(outState);
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
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
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
    private void playAudio(int soundResourceId) {
        // Request for focus. requestAudioFocus(OnAudioFocusChangeListener l, int streamType, int durationHint)
        int focus = mAudioManager.requestAudioFocus(audioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        // Play audio if focus request granted
        if (focus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            mMediaPlayer = MediaPlayer.create(this, soundResourceId);
            mMediaPlayer.start();

            mMediaPlayer.setOnCompletionListener(mCompletionListener);
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

//                mMediaPlayer.setOnCompletionListener(mCompletionListener);
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
            //String audioUrl = "http://odifek.tk/hymnbook_mid/" + currentNum + ".mid";
            //downloadAudio(audioUrl);
        }
        //else mediaPlayer = null;

        return audioUri1;

    }

    /**
     * The {@link CursorPagerAdapter }
     */
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
            mActionBar.setTitle(mCurrentFragment.getHymnTitle());
            mActionBar.setSubtitle(mCurrentFragment.getHymnTopic());
        }

        HymnDetailFragment getCurrentFragment() {
            return mCurrentFragment;
        }
    }
}
