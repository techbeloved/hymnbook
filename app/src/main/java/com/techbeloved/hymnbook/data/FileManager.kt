package com.techbeloved.hymnbook.data

import io.reactivex.Observable

interface FileManager {

    /**
     * Unzips the source file into the destination directory
     * @param source File to unzip
     * @param destinationDir destination folder where the contents will be put
     * @return number of files in the archive
     */
    fun unzipFile(source: String, destinationDir: String): Observable<Long>

    /**
     * Deletes all files in the given directory
     * @return number of files deleted
     */
    fun deleteAllFilesInDir(dir: String): Observable<Long>

    /**
     * Copy file from the specified source to the destination
     * @param source is the file to be copied
     * @param destination is the directory where the file will be placed
     * @param newFileName is the new file name to be given the copied file. If empty, uses the original name
     * @return boolean showing whether the copy is successful
     */
    fun copyFile(source: String, destination: String, newFileName: String = ""): Observable<Boolean>

}