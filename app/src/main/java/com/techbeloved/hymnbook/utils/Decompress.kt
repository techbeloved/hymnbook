package com.techbeloved.hymnbook.utils

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import javax.inject.Inject

class Decompress @Inject constructor() {
    fun unzip(zipFilePath: String, destinationDirPath: String) {
        val zipFile = ZipFile(zipFilePath)
        val entries = zipFile.entries()

        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()

            val entryPath = File(destinationDirPath, entry.name)
            if (entry.isDirectory) {
                entryPath.mkdirs()
            } else {
                entryPath.createNewFile()

                val inputStream = zipFile.getInputStream(entry)
                val outputStream = FileOutputStream(entryPath)

                inputStream.copyTo(outputStream)

                inputStream.close()
                outputStream.close()
            }
        }

        zipFile.close()
    }

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
