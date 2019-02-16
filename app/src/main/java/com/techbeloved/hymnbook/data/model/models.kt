package com.techbeloved.hymnbook.data.model

import androidx.room.DatabaseView

@DatabaseView("SELECT num, title FROM hymns", viewName = "hymn_title")
class HymnTitle(val id: Int, val title: String)