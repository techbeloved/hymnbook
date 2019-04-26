package com.techbeloved.hymnbook.data.model

import androidx.annotation.IntDef


@IntDef(DOWNLOADED, DOWNLOAD_FAILED, DOWNLOAD_IN_PROGRESS, READY, NONE)
@Retention(AnnotationRetention.SOURCE)
annotation class CatalogStatus

const val DOWNLOADED = 10
const val READY = 11
const val DOWNLOAD_IN_PROGRESS = 12
const val DOWNLOAD_FAILED = 21
const val NONE = 0
