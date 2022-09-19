package com.techbeloved.hymnbook.data.analytics

import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class AppAnalytics @Inject constructor(private val firebaseAnalytics: FirebaseAnalytics) {

    fun logEvent(name: String = "userEvent", data: List<Pair<String, Any>>) {
        val bundle = bundleOf(
            *data.toTypedArray()
        )
        firebaseAnalytics.logEvent(name, bundle)
    }
}