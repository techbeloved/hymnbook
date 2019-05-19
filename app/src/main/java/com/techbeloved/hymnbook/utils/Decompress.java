package com.techbeloved.hymnbook.utils;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author jon
 * <p>
 * Usage
 * String zipFile = Environment.getExternalStorageDirectory() + "/the_raven.zip"; //your zip file location
 * String unzipLocation = Environment.getExternalStorageDirectory() + "/unzippedtestNew/"; // destination folder location
 * DecompressFast df= new DecompressFast(zipFile, unzipLocation);
 * df.unzip();
 * <p>
 * Add permissions
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
 */
public class Decompress {
    private String _zipFile;
    private String _location;

    public Decompress(String zipFile, String location) {
        _zipFile = zipFile;
        _location = location;

        _dirChecker("");
    }

    public void unzip() throws Exception {

        FileInputStream fin = new FileInputStream(_zipFile);
        ZipInputStream zin = new ZipInputStream(fin);
        ZipEntry ze;
        while ((ze = zin.getNextEntry()) != null) {

            if (ze.isDirectory()) {
                _dirChecker(ze.getName());
            } else {
                FileOutputStream fout = new FileOutputStream(_location + "/" + ze.getName());
                BufferedOutputStream bufout = new BufferedOutputStream(fout);
                byte[] buffer = new byte[1024];
                int n;
                while ((n = zin.read(buffer)) != -1) {
                    bufout.write(buffer, 0, n);
                }

                zin.closeEntry();
                bufout.close();
                fout.close();
            }

        }
        zin.close();


        File file = new File(_zipFile);
        file.delete();
    }

    private void _dirChecker(String dir) {
        File f = new File(_location + dir);

        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }
}