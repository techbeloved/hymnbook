package com.techbeloved.hymnbook.data

import io.reactivex.Observable
import io.reactivex.Single
import java.io.File

interface FileManager {

    /**
     * Unzips the source file into the destination directory
     * @param source File to unzip
     * @param destinationDir destination folder where the contents will be put
     * @return number of files in the archive
     */
    fun unzipFile(source: String, destinationDir: String): Single<String>

    /**
     * Deletes all files in the given directory
     * @return number of files deleted
     */
    fun deleteAllFilesInDir(dir: File): Observable<Long>

    /**
     * Copy file from the specified source to the destination
     * @param source is the file to be copied
     * @param destination is the directory where the file will be placed
     * @param newFileName is the new file name to be given the copied file. If empty, uses the original name
     * @return boolean showing whether the copy is successful
     */
    fun copyFile(source: String, destination: String, newFileName: String = ""): Observable<Boolean>

    /**
     * Handles unzipping of the files. Actually, it should call an intent service to do it in the background
     * It then monitors the status of the job maybe through a shared preference which is updated when the intent services completes its job
     */
    fun processDownloadZipFiles(vararg zipFiles: String): Observable<ProcessZipStatus>

}

sealed class ProcessZipStatus {
    object Success : ProcessZipStatus()
    data class Failure(val error: Throwable) : ProcessZipStatus()
}