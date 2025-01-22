package com.techbeloved.hymnbook.shared.files

import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream

private val ignoreList = listOf("macosx", "ds_store")

internal class Decompress {
    fun unzip(zipInputStream: ZipInputStream, destinationDirPath: File) {
        zipInputStream.use { inputStream ->
            generateSequence {
                inputStream.nextEntry
            }.filter { entry ->
                ignoreList.none {
                    entry.name.lowercase().contains(it, ignoreCase = true)
                }
            }.forEach { entry ->
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
            }
        }
    }
}
