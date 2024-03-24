package com.techbeloved.hymnbook.shared

import app.cash.sqldelight.db.SqlDriver
import com.techbeloved.hymnbook.Database

expect fun testDatabaseDriver(): SqlDriver

fun Database.deleteAll() {
    songEntityQueries.deleteAll()
    authorEntityQueries.deleteAll()
    songbookEntityQueries.deleteAll()
    topicEntityQueries.deleteAll()
}
