package com.techbeloved.hymnbook;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.TextView;

import static com.techbeloved.hymnbook.data.HymnContract.HymnEntry;

public class HymnDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;
    public static String hymn_tag = "hymn_number";
    private Uri mUri;
    private WebView mDetailWebView;
    private TextView mToolBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hymn_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Remove default title
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolBarTitle = findViewById(R.id.toolbar_title);


        // Get uri sent by hymn list
        mUri = getIntent().getData();
        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        switch (id) {
            case LOADER_ID:
                return new CursorLoader(this, mUri, null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }
        String htmlBodyTemplate = getString(R.string.html_body_template);

        data.moveToFirst();
        long hymn_no = ContentUris.parseId(mUri);
        String title = data.getString(data.getColumnIndexOrThrow(HymnEntry.COLUMN_TITLE));
        String content = data.getString(data.getColumnIndexOrThrow(HymnEntry.COLUMN_CONTENT));

        // Insert the hymn number, title and content in the html template. lol
        String webData = String.format(htmlBodyTemplate, hymn_no, title, content);
        mDetailWebView = findViewById(R.id.detail_webview);

        // Add some css goodness
        String css_link = getString(R.string.css_link);
        webData = css_link + webData;

        mDetailWebView.loadDataWithBaseURL("file:///android_asset/",
                webData, "text/html", "UTF-8", null);

        // Set the title of the tool bar
        String toolBarTitle = hymn_no + ". " + title;
        mToolBarTitle.setText(toolBarTitle);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }


//    private String  buildHtml(){
//        List<String> lines = new ArrayList<>();
//        List<TagNode> liNodes = new ArrayList<>();
//
//        for (int i = 0; i < 5; i++) {
//            lines.add("Item_" + i);
//        }
//
//        TagNode html = new TagNode("html");
//        for (String line : lines){
//            TagNode li = new TagNode("li");
//            li.addChild(new ContentNode(line));
//            liNodes.add(li);
//        }
//        html.addChild(liNodes);
//
//    }
}
