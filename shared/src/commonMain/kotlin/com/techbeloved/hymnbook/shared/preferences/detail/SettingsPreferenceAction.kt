package com.techbeloved.hymnbook.shared.preferences.detail

internal sealed interface SettingsPreferenceAction {
    sealed interface FontSize: SettingsPreferenceAction {
        data object Increase: FontSize

        data object Decrease: FontSize
    }
}
