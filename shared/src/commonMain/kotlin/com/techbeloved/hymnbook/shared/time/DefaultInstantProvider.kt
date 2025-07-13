package com.techbeloved.hymnbook.shared.time

import me.tatarka.inject.annotations.Inject
import kotlin.time.Clock
import kotlin.time.Instant

public class DefaultInstantProvider @Inject constructor() : InstantProvider {
    override fun get(): Instant = Clock.System.now()
}
