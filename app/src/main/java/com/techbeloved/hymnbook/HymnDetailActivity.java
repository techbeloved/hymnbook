package com.techbeloved.hymnbook;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.webkit.WebView;

import org.htmlcleaner.ContentNode;
import org.htmlcleaner.TagNode;

import java.util.ArrayList;
import java.util.List;

public class HymnDetailActivity extends AppCompatActivity {

    public static String hymn_tag = "hymn_number";
    private static String css_link = "<link rel=\"stylesheet\" href=\"style.css\">";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hymn_detail);
        Intent intent = getIntent();

        String webData = getResources().getString(R.string.hymn_sample2);
        WebView webView = findViewById(R.id.detail_webview);
        webData = css_link + webData;

        webView.loadDataWithBaseURL("file:///android_asset/",
                webData, "text/html", "UTF-8", null);
//            webView.loadUrl("file:///android_asset/index.html");

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
