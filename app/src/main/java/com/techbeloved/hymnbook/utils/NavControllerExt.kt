package com.techbeloved.hymnbook.utils

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import timber.log.Timber

fun NavController.safeNavigate(destination: NavDirections) {
    try {
        navigate(destination)
    } catch (e: Exception) {
        Timber.w(e)
    }
}

fun NavController.safeNavigate(destination: Uri) {
    try {
        navigate(destination)
    } catch (e: Exception) {
        Timber.w(e)
    }
}

fun NavController.safeNavigate(destination: Int) {
    try {
        navigate(destination)
    } catch (e: Exception) {
        Timber.w(e)
    }
}