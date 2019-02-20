package com.techbeloved.hymnbook.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "topics")
data class Topic(
        @PrimaryKey
        val id: Int,
        val topic: String
) : Serializable