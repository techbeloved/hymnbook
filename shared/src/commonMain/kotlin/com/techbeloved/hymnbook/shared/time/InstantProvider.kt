package com.techbeloved.hymnbook.shared.time

import kotlin.time.Instant

public fun interface InstantProvider {
    public fun get(): Instant
}
