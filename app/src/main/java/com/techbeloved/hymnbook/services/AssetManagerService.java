package com.techbeloved.hymnbook.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.techbeloved.hymnbook.hymns.MainActivity;
import com.techbeloved.hymnbook.utils.FileAssetManager;

/**
 * Created by kennedy on 5/11/18.
 * Handles copying of the file assets such as midi files in the background
 */

public class AssetManagerService extends IntentService {

    public static final String TAG = AssetManagerService.class.getSimpleName();

    private boolean filesCopied;

    public AssetManagerService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            int newVersion = intent.getIntExtra(MainActivity.MIDI_VERSION, 0);
            // Do the file copying in the background
            Log.i(TAG, "onHandleIntent: service started");
            Toast.makeText(getApplicationContext(), "Start copying tunes to sdcard", Toast.LENGTH_SHORT).show();
            filesCopied = FileAssetManager.copyAssets(this, 0, newVersion);
        }
    }

    @Override
    public void onDestroy() {
        String message = "Error copying media files";
        if (filesCopied) {
            message = "tunes successfully copied to sdcard";
        }
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
