package com.techbeloved.hymnbook.utils;


import android.content.Context;
import android.content.res.AssetManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by kennedy on 5/5/18.
 */

public class FileAssetManager {
    private static final String TAG = FileAssetManager.class.getSimpleName();
    /**
     * A zip file extractor
     * https://stackoverflow.com/questions/3382996/how-to-unzip-files-programmatically-in-android
     * <p>
     * Add necessary permissions
     * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
     * <p>
     * Use like this
     * unzip(new File("/sdcard/pictures.zip"), new File("/sdcard"));
     *
     * @param zipFile         is the zip file File object
     * @param targetDirectory is the target directory
     * @throws IOException
     */
    public static void unzip(File zipFile, File targetDirectory) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(
                new BufferedInputStream(new FileInputStream(zipFile)))) {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(targetDirectory, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        }
    }

    /**
     * Takes a zipFile and decompresses it
     *
     * @param context        is the current application context
     * @param filename       is the zip file name you want to decompress
     * @param extDestination is the location directory
     */
    public static void deCompressArchive(Context context, final String filename, String extDestination) {
        // Do it in a new thread so that the UI is not freezed!
        new Thread(() -> {
            String zipFile;
            String unzipLocation;
            zipFile = ContextCompat.getExternalFilesDirs(context, null)[0] + "/" + filename;
            if (extDestination != null && !extDestination.isEmpty()) {
                unzipLocation = ContextCompat.getExternalFilesDirs(context, null)[0] + "/" + extDestination + "/";
            } else {
                // Just do it in the default location
                unzipLocation = ContextCompat.getExternalFilesDirs(context, null)[0] + "/";
            }
            Decompress d = new Decompress(zipFile, unzipLocation);
            d.unzip();
        }).start();

    }

    /**
     * Takes care of outdated files such as zip or expansion files you want to remove
     *
     * @param context  is the application {@link Context}
     * @param fileName is the name of the {@link File}
     */
    public static void removeIfOutdated(Context context, String fileName) {
        //File file = new File(ContextCompat.getExternalFilesDirs(this, null)[0], fileName);
        File[] availableFiles;
        availableFiles = ContextCompat.getExternalFilesDirs(context, null)[0].listFiles();
        for (File archive : availableFiles) {
            if (!archive.getName().equals(fileName)) {
                archive.delete();
            }
        }
        //if (file.getName() == currFileVersion + fileName)
    }

    /**
     * Checks the file specified exists in the external directory
     *
     * @param context  is the Application context
     * @param fileName is the name of the file requested
     * @return
     */
    public static boolean hasExternalStoragePrivateFile(Context context, String fileName) {
        // Get path for the file on external storage.  If external
        // storage is not currently mounted this will fail.
        File file = new File(ContextCompat.getExternalFilesDirs(context, null)[0], fileName);
        return file != null && file.exists();
    }

    public static boolean copyAssets(Context context, int oldVersion, int newVersion) {
        AssetManager assetManager = context.getAssets();
        String[] files;
        try {
            files = assetManager.list("midi");
        } catch (IOException e) {
            Log.e(TAG, "copyAssets: Failed!", e);
            return false;
        }

        for (String filename : files) {
            //System.out.println("File name => "+filename);
            Log.i(TAG, "copyAssets: File name => " + filename);
            InputStream in;
            OutputStream out;
            // Check if old version of the file is available and remove it if so
            File file = new File(ContextCompat.getExternalFilesDirs(context, null)[0], oldVersion + "_" +
                    filename);
            if (oldVersion != newVersion) {
                removeIfOutdated(context, oldVersion + "_" + filename);
            }
            if (hasExternalStoragePrivateFile(context, oldVersion + "_" + filename))
                continue; //If file already exists,
            // check next or continue
            try {
                in = assetManager.open("midi/" + filename);   // if files resides inside the "Files" directory itself
                out = new FileOutputStream(file);

                copyFile(in, out);
                in.close();
                out.flush();
                out.close();
            } catch (Exception e) {
                Log.e("tag", e.getMessage());
            }
            deCompressArchive(context, newVersion + "_" + filename, "midi");
        }
        return true;
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

}