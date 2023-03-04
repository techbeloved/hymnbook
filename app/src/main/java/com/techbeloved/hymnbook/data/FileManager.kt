package com.techbeloved.hymnbook.data

import io.reactivex.Single

interface FileManager {

    /**
     * Unzips the source file into the destination directory
     * @param source File to unzip
     * @param destinationDir destination folder where the contents will be put
     * @return number of files in the archive
     */
    fun unzipFile(source: String, destinationDir: String): Single<String>

}
