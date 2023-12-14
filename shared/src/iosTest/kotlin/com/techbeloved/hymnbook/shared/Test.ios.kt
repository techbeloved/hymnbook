package com.techbeloved.hymnbook.shared

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.wrapConnection
import co.touchlab.sqliter.DatabaseConfiguration
import com.techbeloved.hymnbook.Database
import kotlin.test.Test
import kotlin.test.assertTrue

class IosGreetingTest {

    @Test
    fun testExample() {
        assertTrue(Greeting().greet().contains("iOS"), "Check iOS is mentioned")
    }
}

actual fun testDatabaseDriver(): SqlDriver = NativeSqliteDriver(
    Database.Schema,
    name = "test_app.db",
    onConfiguration = {
        DatabaseConfiguration(
            name = null,
            version = Database.Schema.version.toInt(),
            create = { wrapConnection(it, Database.Schema::create) },
            upgrade = { connection, oldVersion, newVersion ->
                wrapConnection(connection) { Database.Schema.migrate(it, oldVersion.toLong(), newVersion.toLong()) }
            },
            inMemory = true,
        )
    })

