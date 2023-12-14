package com.techbeloved.hymnbook.shared

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.techbeloved.hymnbook.Database

actual fun testDatabaseDriver(): SqlDriver =
    JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).apply { Database.Schema.create(this) }