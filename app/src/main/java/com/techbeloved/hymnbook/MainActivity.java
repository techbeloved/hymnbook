package com.techbeloved.hymnbook;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.techbeloved.hymnbook.utils.FileAssetManager;

public class MainActivity extends AppCompatActivity {
    public static final String MIDI_READY = "MidiFilesReady";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String PREF_NAME = "MyPreferences";

    public static void saveMidiFilesReadyPrefs(String key, boolean value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    private static boolean getMidiFilesReadyPrefs(String key, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        return sharedPreferences.getBoolean(key, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.main_viewpager);
        if (viewPager != null){
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        // Copy midi assets if not already copied
        if (!getMidiFilesReadyPrefs(MIDI_READY, this)) {
            Log.i(TAG, "onCreate: about copying midi files");
            if (FileAssetManager.copyAssets(this, 0, 1)) {
                saveMidiFilesReadyPrefs(MIDI_READY, true, this);
            }
        }

    }

    private void setupViewPager(ViewPager viewPager) {
        HymnMainPagerAdapter adapter = new HymnMainPagerAdapter(this, getSupportFragmentManager());
        adapter.addFragment(new HymnTitlesFragment(), "All hymns");
        adapter.addFragment(new TopicsFragment(), "Topics");
        adapter.addFragment(new FavoritesFragment(), "Favourites");
        viewPager.setAdapter(adapter);
    }
}
