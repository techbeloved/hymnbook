package com.techbeloved.hymnbook.hymns;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.tabs.TabLayout;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import com.techbeloved.hymnbook.R;
import com.techbeloved.hymnbook.services.AssetManagerService;
import com.techbeloved.hymnbook.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {
    public static final String MIDI_READY = "MidiFilesReady";
    public static final String PREF_NAME = "MyPreferences";
    public static final String MIDI_VERSION = "midiVersion";
    // Update this whenever the midi files are updated in the zip file
    private static final int CURRENT_MIDI_VERSION = 2;

    public static void saveMidiFilesReadyPrefs(String key, boolean value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void saveMidiFilesVersion(String key, int newVersion, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, newVersion);
        editor.apply();
    }

    private static boolean getMidiFilesReadyPrefs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        return sharedPreferences.getBoolean(MainActivity.MIDI_READY, false);
    }

    private static int getMidiFilesVersion(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREF_NAME, 0);
        return sharedPreferences.getInt(MainActivity.MIDI_VERSION, 0);
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

        // Copy midi assets if not already copied or the midi file version has been updated
        if (!getMidiFilesReadyPrefs(getApplicationContext()) ||
                CURRENT_MIDI_VERSION != getMidiFilesVersion(getApplicationContext())) {

            Intent intent = new Intent(this, AssetManagerService.class);
            intent.putExtra(MIDI_VERSION, CURRENT_MIDI_VERSION);
            startService(intent);

            saveMidiFilesReadyPrefs(MIDI_READY, true, getApplicationContext());
            saveMidiFilesVersion(MIDI_VERSION, CURRENT_MIDI_VERSION, getApplicationContext());
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

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
        searchView.setIconifiedByDefault(false);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
