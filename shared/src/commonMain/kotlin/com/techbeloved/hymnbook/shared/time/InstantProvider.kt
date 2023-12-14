package com.techbeloved.hymnbook.shared.time

import kotlinx.datetime.Instant

public fun interface InstantProvider {
    public fun get(): Instant
}
