package com.techbeloved.hymnbook.data.model

import androidx.annotation.StringDef
import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "hymns")
data class Hymn(var id: String, @PrimaryKey var num: Int, var title: String, var verses: List<String>, var first: String): Serializable {

//    @PrimaryKey(autoGenerate = true)
//    @ColumnInfo(name = "rowid")
//    var rowId: Int = 1
    var chorus: String? = null
    @SerializedName("topic")
    var topicId: Int = 0
    @Embedded
    var audio: Audio? = null
    @SerializedName("sheet_music")
    var sheetMusic: String? = null
    @SerializedName("video")
    var videoUrl: String? = null
    @Embedded
    var attribution: Attribution? = null

    class Audio: Serializable {
        lateinit var midi: String
        lateinit var mp3: String

        @Ignore
        constructor(midi: String, mp3: String) {
            this.midi = midi
            this.mp3 = mp3
        }

        constructor() {

        }

    }

    class Attribution: Serializable {
        @SerializedName("music_by")
        var musicBy: String? = null
        @SerializedName("lyrics_by")
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

/*
{
  "hymns" : [
    {
      "num": "1",
      "verses": ["verse1", "verse2", "verse3"],
      "chorus": "chorus",
      "topic": "topic1",
      "id": "hymn_1",
      "audio": {
        "midi": "link_to_midi",
        "mp3": "link_to_mp3"
      },
      "sheet_music": "link_to_sheet_music",
      "video": "link_to_video_if_any",
      "attribution": {
        "music_by": "Created by",
        "lyrics_by": "Lyrics by",
        "credits": "Credited to"
      }
    }
    ],
    "topics": [
      {
        "num": "1",
        "title": "topic1"
      }
      ]
}
 */
