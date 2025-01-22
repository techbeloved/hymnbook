package com.techbeloved.hymnbook.shared.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.techbeloved.hymnbook.Database
import java.io.File
import java.util.Properties

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), "hymnbook_songs.db")
        val driver: SqlDriver = JdbcSqliteDriver(
            url = "jdbc:sqlite:" + databasePath.absolutePath,
            properties = Properties().apply { put("foreign_keys", "true") },
        )
        Database.Schema.create(driver)
        return driver
    }
}

internal actual fun getDriverFactory(): DriverFactory {
    return DriverFactory()
}
