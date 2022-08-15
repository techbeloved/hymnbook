package com.techbeloved.hymnbook.data.model

import androidx.annotation.IntDef
import androidx.annotation.StringDef
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import java.io.Serializable

@kotlinx.serialization.Serializable
@Entity(tableName = "hymns")
data class Hymn(var id: String, @PrimaryKey var num: Int, var title: String, var verses: List<String>, var first: String): Serializable {

    var chorus: String? = null
    @SerialName("topic")
    var topicId: Int = 0
    @Embedded
    var audio: Audio? = null

    @Embedded
    var sheetMusic: SheetMusic? = null
    @SerialName("video")
    var videoUrl: String? = null
    @Embedded
    var attribution: Attribution? = null

    @kotlinx.serialization.Serializable
    data class SheetMusic(@Status val downloadStatus: Int,
                          val downloadProgress: Int = 0,
                          val remoteUri: String? = null,
                          val localUri: String? = null)

    @kotlinx.serialization.Serializable
    class Audio: Serializable {
        var midi: String? = null
        var mp3: String? = null

        @Ignore
        constructor(midi: String, mp3: String) {
            this.midi = midi
            this.mp3 = mp3
        }

        constructor()

    }

    @kotlinx.serialization.Serializable
    class Attribution: Serializable {
        @SerialName("music_by")
        var musicBy: String? = null
        @SerialName("lyrics_by")
        var lyricsBy: String? = null
        var credits: String? = null

        inner class Builder {
            private val mAttribution = Attribution()

            fun musicBy(musicAuthor: String): Builder {
                mAttribution.musicBy = musicAuthor
                return this
            }

            fun lyricsBy(lyricsAuthor: String): Builder {
                mAttribution.lyricsBy = lyricsAuthor
                return this
            }

            fun credits(creditedBy: String): Builder {
                mAttribution.credits = creditedBy
                return this
            }

            fun build(): Attribution {
                return this.mAttribution
            }
        }
    }


    companion object {
        const val COL_NUM = "num"
        const val COL_TITLE = "title"

        @StringDef(COL_NUM, COL_TITLE)
        @Retention(AnnotationRetention.SOURCE)
        annotation class ColumnName
    }
}

@IntDef(READY, DOWNLOADED, DOWNLOAD_IN_PROGRESS, DOWNLOAD_FAILED, NONE)
@Retention(AnnotationRetention.SOURCE)
annotation class Status

