package com.techbeloved.hymnbook.shared.time

import kotlinx.datetime.TimeZone
import me.tatarka.inject.annotations.Inject

public class DefaultTimeZoneProvider @Inject constructor() : TimeZoneProvider {
    override fun get(): TimeZone = TimeZone.currentSystemDefault()
}
