package com.techbeloved.hymnbook.data.model

/**
 * Used to synchronise the local data with remote music information
 */
data class OnlineMusicUpdate(
    val num: Int,
    // The sheet music pdf uri
    val remoteUri: String
)
