package com.techbeloved.hymnbook.shared.data

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.shared.di.AndroidInjector
import io.requery.android.database.sqlite.RequerySQLiteOpenHelperFactory

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver {
        val factory = RequerySQLiteOpenHelperFactory()
        return AndroidSqliteDriver(
            schema = Database.Schema,
            context = context,
            name = "shared_songs.db",
            callback = object : AndroidSqliteDriver.Callback(Database.Schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.setForeignKeyConstraintsEnabled(true)
                }
            },
            factory = factory,
        )
    }
}

internal actual fun getDriverFactory(): DriverFactory {
    return DriverFactory(AndroidInjector.application)
}
