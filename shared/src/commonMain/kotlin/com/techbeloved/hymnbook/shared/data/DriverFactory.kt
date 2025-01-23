package com.techbeloved.hymnbook.shared.data

import app.cash.sqldelight.db.SqlDriver

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect class DriverFactory {
    fun createDriver(): SqlDriver
}

internal expect fun getDriverFactory(): DriverFactory
