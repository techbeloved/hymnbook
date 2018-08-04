package com.techbeloved.hymnbook.about;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.techbeloved.hymnbook.R;

import java.util.ArrayList;
import java.util.Objects;

public class AcknowledgementsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgements);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.acknowledgements);

        mRecyclerView = findViewById(R.id.library_recycler_view);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        mAdapter = new LicenseAdapter()

        ArrayList<Library> libraries = new ArrayList<>();
        libraries.add(new Library(
                "Android Support Libraries",
                "Copyright Google Inc.",
                "Apache License, Version 2.0"
        ));
        libraries.add(new Library(
                "google-gson",
                "Copyright 2008 Google Inc",
                "Apache License, Version 2.0"
        ));
        libraries.add(new Library(
                        "Android SQLiteAssetHelper",
                        "Copyright (c) 2012 readyState Software Ltd.",
                        "Apache Software License 2.0"
                )
        );
        libraries.add(new Library(
                "HtmlCleaner",
                "Copyright (c) 2006-2018, HtmlCleaner team",
                "BSD License"
        ));

        mAdapter = new LicenseAdapter(libraries, library -> {

        });

        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
