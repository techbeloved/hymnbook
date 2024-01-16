package com.techbeloved.hymnbook.shared.files

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

internal class Decompress {
    fun unzip(zipInputStream: ZipInputStream, destinationDirPath: File) {
        zipInputStream.use { inputStream ->
            var entry = inputStream.nextEntry
            while (entry != null) {
                val entryPath = File(destinationDirPath, entry.name)
                if (entry.isDirectory) {
                    entryPath.mkdirs()
                } else {
                    if (entryPath.exists()) {
                        entryPath.delete()
                    }
                    val fileOutputStream = FileOutputStream(entryPath)
                    inputStream.copyTo(fileOutputStream)
                    fileOutputStream.close()
                }
                entry = inputStream.nextEntry
            }
        }
    }
}