package com.techbeloved.hymnbook;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

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
    }

    private void setupViewPager(ViewPager viewPager) {
        HymnMainPagerAdapter adapter = new HymnMainPagerAdapter(this, getSupportFragmentManager());
        adapter.addFragment(new HymnTitlesFragment(), "All hymns");
        adapter.addFragment(new TopicsFragment(), "Topics");
        adapter.addFragment(new FavoritesFragment(), "Favourites");
        viewPager.setAdapter(adapter);
    }
}
