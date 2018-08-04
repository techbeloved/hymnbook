package com.techbeloved.hymnbook.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.techbeloved.hymnbook.R;
import com.techbeloved.hymnbook.about.AboutActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    private static final String FILENAME = "feedback.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.action_settings);

        LinearLayout mAboutLayout = findViewById(R.id.about_layout);
        mAboutLayout.setOnClickListener(view -> {
            // Start the about activity
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
        });

        TextView tvFeedback = findViewById(R.id.feedback_text);
        tvFeedback.setOnClickListener(view -> {
            File feedbackTextFile = createFileWithContent(getDeviceInfo());
            sendFeedbackToGmail(feedbackTextFile);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private String getDeviceInfo() {
        return "API Level: " + String.valueOf(Build.VERSION.SDK_INT) +
                "\nAndroid Release: " + Build.VERSION.RELEASE +
            "\nDevice: " + Build.DEVICE +
            "\nModel: " + Build.MODEL   +
            "\nProduct: " + Build.PRODUCT;
    }

    private void sendFeedbackToGmail(File feedbackAttachment) {
        if (feedbackAttachment != null) {
            Intent email = new Intent(Intent.ACTION_SEND);
            email.putExtra(Intent.EXTRA_EMAIL, new String[] {getString(R.string.developer_email)});
            email.putExtra(Intent.EXTRA_SUBJECT, "Hymnbook app feedback");
            email.putExtra(Intent.EXTRA_TEXT, "Thank you");
            email.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + feedbackAttachment.getAbsoluteFile()));
            email.setType(getString(R.string.email_mime_type));
            if (email.resolveActivity(getPackageManager()) != null) {
                startActivity(Intent.createChooser(email, "Send Feedback"));
            }
        }
    }

    private File createFileWithContent(String content) {
        if (TextUtils.isEmpty(content)) {
            content = "No information gotten";
        }
        File file = null;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), FILENAME);
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
