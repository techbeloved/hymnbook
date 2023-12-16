package com.techbeloved.hymnbook.shared.time

import kotlinx.datetime.TimeZone

public class DefaultTimeZoneProvider : TimeZoneProvider {
    override fun get(): TimeZone = TimeZone.currentSystemDefault()
}
