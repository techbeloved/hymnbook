package com.techbeloved.hymnbook.shared.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject

public class DefaultInstantProvider @Inject constructor() : InstantProvider {
    override fun get(): Instant = Clock.System.now()
}
