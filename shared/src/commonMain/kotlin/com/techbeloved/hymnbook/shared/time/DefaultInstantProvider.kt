package com.techbeloved.hymnbook.shared.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

public class DefaultInstantProvider : InstantProvider {
    override fun get(): Instant = Clock.System.now()
}
