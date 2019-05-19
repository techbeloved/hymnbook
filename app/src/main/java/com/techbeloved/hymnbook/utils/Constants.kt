@file:JvmName("Constants")

package com.techbeloved.hymnbook.utils

// Notification Channel constants

// Name of Notification Channel for verbose notifications of background work
@JvmField
val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence =
        "Verbose Hymnbook Notifications"
const val VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION =
        "Shows notifications whenever work starts"
@JvmField
val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
const val CHANNEL_ID = "VERBOSE_HYMNBOOK_NOTIFICATION"
const val NOTIFICATION_ID = 1