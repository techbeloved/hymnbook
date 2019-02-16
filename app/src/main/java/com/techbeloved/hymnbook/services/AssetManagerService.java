package com.techbeloved.hymnbook.services;

import android.app.IntentService;
import android.content.Intent;
import androidx.annotation.Nullable;

import com.techbeloved.hymnbook.R;
import com.techbeloved.hymnbook.hymns.MainActivity;
import com.techbeloved.hymnbook.utils.FileAssetManager;

import static xdroid.toaster.Toaster.toast;

/**
 * Created by kennedy on 5/11/18.
 * Handles copying of the file assets such as midi files in the background
 */

public class AssetManagerService extends IntentService {

    public static final String TAG = AssetManagerService.class.getSimpleName();

    public AssetManagerService() {
        super(TAG);
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            int newVersion = intent.getIntExtra(MainActivity.MIDI_VERSION, 0);
            // Do the file copying in the background
            new Thread(() -> toast(R.string.start_copy_tunes_msg)).start();
            FileAssetManager.copyAssets(getApplicationContext(), 0, newVersion);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
