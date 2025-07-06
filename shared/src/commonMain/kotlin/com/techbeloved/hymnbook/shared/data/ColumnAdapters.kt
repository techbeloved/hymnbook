package com.techbeloved.hymnbook.shared.data

import app.cash.sqldelight.ColumnAdapter
import kotlin.time.Instant
import kotlinx.serialization.json.Json

internal inline fun <reified T> listColumnAdapter(json: Json) =
    object : ColumnAdapter<List<T>, String> {
        override fun decode(databaseValue: String): List<T> = json.decodeFromString(databaseValue)

        override fun encode(value: List<T>): String = json.encodeToString(value)
    }

internal inline fun <reified T : Any> jsonColumnAdapter(json: Json) =
    object : ColumnAdapter<T, String> {
        override fun decode(databaseValue: String): T = json.decodeFromString(databaseValue)

        override fun encode(value: T): String = json.encodeToString(value)
    }

internal fun dateColumnAdapter() = object : ColumnAdapter<Instant, Long> {
    override fun decode(databaseValue: Long): Instant = Instant.fromEpochMilliseconds(databaseValue)

    override fun encode(value: Instant): Long = value.toEpochMilliseconds()

}
