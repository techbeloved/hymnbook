package com.techbeloved.hymnbook.shared.songshare

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

public object DeeplinkHandler {
    private val _deeplinks = MutableStateFlow<String?>(null)
    internal val deeplinks = _deeplinks.asStateFlow()

    public fun setDeeplink(deeplink: String?) {
        _deeplinks.update { deeplink }
    }

    internal fun clearDeeplink() {
        _deeplinks.update { null }
    }
}
