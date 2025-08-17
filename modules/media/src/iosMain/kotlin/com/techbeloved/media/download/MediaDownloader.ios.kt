package com.techbeloved.media.download

import com.techbeloved.media.getNSURLFromRelativePath
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSURLSession
import platform.Foundation.NSURLSessionConfiguration
import platform.Foundation.NSURLSessionDownloadDelegateProtocol
import platform.Foundation.NSURLSessionDownloadTask
import platform.darwin.NSObject

actual fun getPlatformMediaDownloader(): MediaDownloader = IosMediaDownloader()

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IosMediaDownloader : MediaDownloader {

    override fun download(
        url: String,
        destination: String
    ): Flow<MediaDownloadState> = callbackFlow {
        trySend(MediaDownloadState.Initializing)
        // download using ios apis

        val downloadDelegate = object : NSURLSessionDownloadDelegateProtocol, NSObject() {

            override fun URLSession(
                session: NSURLSession,
                downloadTask: NSURLSessionDownloadTask,
                didWriteData: Long,
                totalBytesWritten: Long,
                totalBytesExpectedToWrite: Long,
            ) {
                val calculatedProgress =
                    (totalBytesWritten / totalBytesExpectedToWrite.toFloat()).coerceIn(
                        maximumValue = 1f,
                        minimumValue = 0f,
                    )
                trySend(MediaDownloadState.Downloading(calculatedProgress))
            }

            override fun URLSession(
                session: NSURLSession,
                downloadTask: NSURLSessionDownloadTask,
                didFinishDownloadingToURL: NSURL
            ) {

                val savedUrl = getNSURLFromRelativePath(destination)
                memScoped {
                    val error = alloc<ObjCObjectVar<NSError?>>()
                    NSFileManager.defaultManager.moveItemAtURL(
                        srcURL = didFinishDownloadingToURL,
                        toURL = checkNotNull(savedUrl),
                        error = error.ptr,
                    )

                    if (error.value != null) {
                        trySend(MediaDownloadState.Error(error.value!!.localizedDescription))
                    } else {
                        trySend(MediaDownloadState.Success)
                    }
                }
                this@callbackFlow.close()
            }
        }


        val configuration = NSURLSessionConfiguration.defaultSessionConfiguration
        val session = NSURLSession.sessionWithConfiguration(
            configuration,
            delegate = downloadDelegate,
            delegateQueue = null
        )
        val downloadUrl = NSURL.URLWithString(url)
        val downloadTask = session.downloadTaskWithURL(checkNotNull(downloadUrl))
        downloadTask.resume()

        awaitClose { downloadTask.cancel() }
    }
}
