package com.techbeloved.hymnbook

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.techbeloved.hymnbook.inappupdates.CheckAppUpdates
import com.techbeloved.hymnbook.shared.App
import com.techbeloved.hymnbook.shared.settings.DarkModePreference
import com.techbeloved.hymnbook.shared.songshare.DeeplinkHandler
import com.techbeloved.hymnbook.shared.ui.settings.GetDarkModePreferenceFlowUseCase
import com.techbeloved.media.DefaultMediaControllerDisposer
import com.techbeloved.media.MediaControllerDisposer
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(),
    MediaControllerDisposer by DefaultMediaControllerDisposer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false)
        }
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        updateDarkModePrefs()

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

    private fun updateDarkModePrefs() {
        lifecycleScope.launch {
            GetDarkModePreferenceFlowUseCase.instance()
                .collect { darkMode ->
                    val uiMode = when(darkMode) {
                        DarkModePreference.Light -> AppCompatDelegate.MODE_NIGHT_NO
                        DarkModePreference.Dark -> AppCompatDelegate.MODE_NIGHT_YES
                        DarkModePreference.System -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    }
                    AppCompatDelegate.setDefaultNightMode(uiMode)
                }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    App()
}
