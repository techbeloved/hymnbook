package com.techbeloved.hymnbook

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.techbeloved.hymnbook.inappupdates.CheckAppUpdates
import com.techbeloved.hymnbook.shared.App
import com.techbeloved.hymnbook.shared.songshare.DeeplinkHandler
import com.techbeloved.media.DefaultMediaControllerDisposer
import com.techbeloved.media.MediaControllerDisposer

class MainActivity : AppCompatActivity(),
    MediaControllerDisposer by DefaultMediaControllerDisposer() {

    private val analytics: FirebaseAnalytics by lazy {
        Firebase.analytics
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        window.setNavigationBarContrastEnforced(false)
        super.onCreate(savedInstanceState)
        analytics

        handleIntent(intent)

        setContent {
            App()
            CheckAppUpdates(appUpdateManager = remember { AppUpdateManagerFactory.create(this) })
        }
    }

    override fun onStop() {
        onDispose()
        super.onStop()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val appLinkAction = intent.action
        val appLinkData = intent.data
        if (Intent.ACTION_VIEW == appLinkAction && appLinkData != null) {
            DeeplinkHandler.setDeeplink(deeplink = appLinkData.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    App()
}
