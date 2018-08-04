package com.techbeloved.hymnbook.hymnsearch;

import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.techbeloved.hymnbook.R;
import com.techbeloved.hymnbook.data.HymnContract;
import com.techbeloved.hymnbook.hymndetail.HymnDetailActivity;

/**
 * Created by kennedy on 5/12/18.
 */

public class SearchResultsActivity extends AppCompatActivity {

    SearchDatabaseTable db = new SearchDatabaseTable(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setTitle(getString(R.string.search_result));
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            //use the query to search your data somehow
            Cursor c = db.getWordMatches(query);

            // Populate the adapter with the search results
            final ListView resultList = findViewById(R.id.list);
            SearchResultsCursorAdapter adapter = new SearchResultsCursorAdapter(this, c);
            resultList.setAdapter(adapter);

            resultList.setOnItemClickListener((parent, view, position, id) -> {
                Uri uri = Uri.withAppendedPath(HymnContract.HymnEntry.CONTENT_URI, String.valueOf(id));
                Intent viewIntent = new Intent(Intent.ACTION_VIEW,
                        uri,
                        parent.getContext(),
                        HymnDetailActivity.class
                );
                startActivity(viewIntent);
            });

            // Configure the empty View to be displayed when there are no results
            View emptyView = findViewById(R.id.empty);
            ImageView emptyImage = findViewById(R.id.empty_search_image);
            TextView emptyTitle = findViewById(R.id.empty_title_text);
            TextView emptySubtTitle = findViewById(R.id.empty_subtitle_text);

            emptyImage.setImageResource(R.drawable.magnifying_glass_funny);
            emptyTitle.setText(R.string.no_results);
            emptySubtTitle.setText(R.string.try_another);

            resultList.setEmptyView(emptyView);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // If UP button is pressed, return to the previous activity
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
