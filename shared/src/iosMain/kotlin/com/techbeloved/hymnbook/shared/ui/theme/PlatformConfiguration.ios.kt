package com.techbeloved.hymnbook.shared.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.uikit.LocalUIViewController
import com.techbeloved.hymnbook.shared.di.appComponent
import com.techbeloved.hymnbook.shared.settings.DarkModePreference
import kotlinx.coroutines.flow.map
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.UIKit.UIAccessibilityContrastHigh
import platform.UIKit.UIAccessibilityDarkerSystemColorsStatusDidChangeNotification
import platform.UIKit.UITraitCollection
import platform.UIKit.currentTraitCollection

@Composable
internal actual fun platformContrastMode(): ContrastMode {

    val isHighContrastEnabled by isHighContrastEnabled()

    return when {
        isHighContrastEnabled -> ContrastMode.High
        else -> ContrastMode.Default
    }
}


@Composable
internal fun isHighContrastEnabled(): State<Boolean> {

    val uiViewController = LocalUIViewController.current
    val isHighContrastState = remember(uiViewController) {
        mutableStateOf(
            uiViewController.traitCollection
                .accessibilityContrast == UIAccessibilityContrastHigh
        )
    }
    DisposableEffect(uiViewController) {
        val contrastDidChangeObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIAccessibilityDarkerSystemColorsStatusDidChangeNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue
        ) { _ ->

            isHighContrastState.value =
                UITraitCollection.currentTraitCollection
                    .accessibilityContrast == UIAccessibilityContrastHigh
        }

        onDispose {
            NSNotificationCenter.defaultCenter.removeObserver(contrastDidChangeObserver)
        }
    }
    return isHighContrastState
}

@Composable
internal actual fun isAppInDarkTheme(): Boolean {
    val defaultState = isSystemInDarkTheme()
    val preferenceFlow = remember {
        appComponent.getDarkModePreferenceFlowUseCase().invoke()
            .map { darkMode ->
                when (darkMode) {
                    DarkModePreference.Light -> false
                    DarkModePreference.Dark -> true
                    DarkModePreference.System -> defaultState
                }
            }
    }

    return preferenceFlow.collectAsState(defaultState).value
}
