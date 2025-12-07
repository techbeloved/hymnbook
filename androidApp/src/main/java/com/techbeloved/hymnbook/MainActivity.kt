package com.techbeloved.hymnbook

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.techbeloved.hymnbook.inappupdates.CheckAppUpdates
import com.techbeloved.hymnbook.shared.App
import com.techbeloved.hymnbook.shared.settings.DarkModePreference
import com.techbeloved.hymnbook.shared.songshare.DeeplinkHandler
import com.techbeloved.media.DefaultMediaControllerDisposer
import com.techbeloved.media.MediaControllerDisposer

private val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)
private val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)

class MainActivity : AppCompatActivity(),
    MediaControllerDisposer by DefaultMediaControllerDisposer() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.setNavigationBarContrastEnforced(false)
        }
        super.onCreate(savedInstanceState)

        handleIntent(intent)

        setContent {
            val isSystemDark = isSystemInDarkTheme()
            var darkModePreference by rememberSaveable { mutableStateOf(DarkModePreference.System) }
            val darkMode by remember {
                derivedStateOf {
                    darkModePreference == DarkModePreference.Dark
                            || (darkModePreference == DarkModePreference.System && isSystemDark)
                }
            }
            LaunchedEffect(darkMode) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        lightScrim = android.graphics.Color.TRANSPARENT,
                        darkScrim = android.graphics.Color.TRANSPARENT,
                    ) { darkMode },
                    navigationBarStyle = SystemBarStyle.auto(
                        lightScrim = lightScrim,
                        darkScrim = darkScrim,
                    ) { darkMode },
                )
            }
            App(onDarkMode = { darkMode ->
                darkModePreference = darkMode
            })
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
