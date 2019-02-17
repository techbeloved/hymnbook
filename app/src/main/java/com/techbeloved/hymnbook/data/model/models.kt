package com.techbeloved.hymnbook.data.model

import androidx.room.ColumnInfo
import androidx.room.DatabaseView

@DatabaseView("SELECT num, title FROM hymns", viewName = "hymn_titles")
data class HymnTitle(@ColumnInfo(name = "num") val id: Int, val title: String)