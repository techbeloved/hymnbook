package com.techbeloved.hymnbook.shared.data

import app.cash.sqldelight.db.SqlDriver

expect class DriverFactory {
    fun createDriver(): SqlDriver

    fun createInMemorySqlDriver(): SqlDriver
}

internal expect fun getDriverFactory(): DriverFactory
