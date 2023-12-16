package com.techbeloved.hymnbook.shared.data

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.di.AndroidInjector

internal actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "shared_songs.db",
            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            },
        )
    }

    actual fun createInMemorySqlDriver(): SqlDriver {
        return AndroidSqliteDriver(
            Database.Schema, context, name = null,
            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            },
        )
    }
}

internal actual fun getDriverFactory(): DriverFactory {
    return DriverFactory(AndroidInjector.application)
}