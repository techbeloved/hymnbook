package com.techbeloved.hymnbook.shared.time

import kotlinx.datetime.TimeZone

public fun interface TimeZoneProvider {
    public fun get(): TimeZone
}
