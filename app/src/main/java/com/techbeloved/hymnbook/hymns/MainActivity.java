package com.techbeloved.hymnbook.hymns;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
    public static final String MIDI_VERSION = "midiVersoin";

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
        if (!getMidiFilesReadyPrefs(MIDI_READY, getApplicationContext())) {
            Intent intent = new Intent(this, AssetManagerService.class);
            intent.putExtra(MIDI_VERSION, 1);
            startService(intent);
            saveMidiFilesReadyPrefs(MIDI_READY, true, getApplicationContext());
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
