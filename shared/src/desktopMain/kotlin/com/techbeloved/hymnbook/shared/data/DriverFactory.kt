package com.techbeloved.hymnbook.shared.data

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.techbeloved.hymnbook.Database
import java.io.File

actual class DriverFactory {
    actual fun createDriver(): SqlDriver {
        val databasePath = File(System.getProperty("user.home"), "hymnbook_songs.db")
        val driver: SqlDriver = JdbcSqliteDriver("jdbc:sqlite:" + databasePath.absolutePath)
        Database.Schema.create(driver)
        return driver
    }

    actual fun createInMemorySqlDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        return driver
    }
}

internal actual fun getDriverFactory(): DriverFactory {
    return DriverFactory()
}