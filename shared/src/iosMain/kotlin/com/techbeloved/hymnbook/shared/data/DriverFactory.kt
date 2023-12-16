package com.techbeloved.hymnbook.shared.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.techbeloved.hymnbook.Database

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(Database.Schema, "songs.db")
    }

    actual fun createInMemorySqlDriver(): SqlDriver {
        return NativeSqliteDriver(Database.Schema, "test.db")
    }
}

internal actual fun getDriverFactory(): DriverFactory {
    return DriverFactory()
}