package com.techbeloved.hymnbook.about;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techbeloved.hymnbook.R;

import java.util.Objects;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.about);

        LinearLayout layoutAboutWCCRM = findViewById(R.id.about_wccrm_layout);
        layoutAboutWCCRM.setOnClickListener(view -> {
            // Start the required activity
            Intent aboutWCCRMIntent = new Intent(this, AboutWccrmActivity.class);
            startActivity(aboutWCCRMIntent);
        });

        TextView tvAcknowledge = findViewById(R.id.acknowledge_text);
        tvAcknowledge.setOnClickListener(view -> {
            Intent acknowledgeIntent = new Intent(this, AcknowledgementsActivity.class);
            startActivity(acknowledgeIntent);
        });

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
