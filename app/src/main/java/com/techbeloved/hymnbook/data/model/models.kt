package com.techbeloved.hymnbook.data.model

import androidx.room.ColumnInfo
import androidx.room.DatabaseView
import androidx.room.Embedded

@DatabaseView("SELECT num, title FROM hymns", viewName = "hymn_titles")
data class HymnTitle(@ColumnInfo(name = "num") val id: Int, val title: String)

@DatabaseView("SELECT *, topic FROM hymns AS h, topics AS t WHERE h.topicId=t.id", viewName = "hymn_with_topics")
data class HymnDetail(var id: String, var num: Int, var title: String, var verses: List<String>, val topic: String) {
    var chorus: String? = null

    var topicId: Int = 0
    @Embedded
    var audio: Hymn.Audio? = null

    var sheetMusic: String? = null

    var videoUrl: String? = null
    @Embedded
    var attribution: Hymn.Attribution? = null
}