package com.techbeloved.hymnbook.shared

import app.cash.sqldelight.db.SqlDriver
import com.techbeloved.hymnbook.Database
import kotlin.test.Test
import kotlin.test.assertTrue

class CommonGreetingTest {

    @Test
    fun testExample() {
        assertTrue(Greeting().greet().contains("Hello"), "Check 'Hello' is mentioned")
    }
}

expect fun testDatabaseDriver(): SqlDriver

fun Database.deleteAll() {
    songEntityQueries.deleteAll()
    authorEntityQueries.deleteAll()
    songbookEntityQueries.deleteAll()
    topicEntityQueries.deleteAll()
}
