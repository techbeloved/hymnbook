package com.techbeloved.hymnbook.shared.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import co.touchlab.sqliter.DatabaseConfiguration
import com.techbeloved.hymnbook.Database

internal actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(
            Database.Schema,
            "songs.db",
            onConfiguration = { config: DatabaseConfiguration ->
                config.copy(
                    extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true)
                )
            })
    }

    actual fun createInMemorySqlDriver(): SqlDriver {
        return NativeSqliteDriver(
            schema = Database.Schema,
            name = "test.db",
            onConfiguration = { config: DatabaseConfiguration ->
                config.copy(
                    extendedConfig = DatabaseConfiguration.Extended(foreignKeyConstraints = true)
                )
            },
        )
    }
}

internal actual fun getDriverFactory(): DriverFactory {
    return DriverFactory()
}