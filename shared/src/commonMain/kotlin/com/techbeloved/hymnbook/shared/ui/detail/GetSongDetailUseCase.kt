package com.techbeloved.hymnbook.shared.ui.detail

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.sp
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.SongDetail
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.dispatcher.getPlatformDispatcherProvider
import com.techbeloved.hymnbook.shared.model.Lyric
import com.techbeloved.hymnbook.shared.model.ext.lyricsByVerseOrder
import com.techbeloved.hymnbook.shared.model.ext.songbookEntries
import kotlinx.coroutines.withContext

internal class GetSongDetailUseCase(
    private val database: Database = Injector.database,
    private val dispatchersProvider: DispatchersProvider = getPlatformDispatcherProvider(),
) {

    suspend operator fun invoke(songId: Long): SongUiDetail = withContext(dispatchersProvider.io()) {
        database.songEntityQueries.getSongById(songId).executeAsOne()
            .toUiDetail()
    }

    private fun SongDetail.toUiDetail(): SongUiDetail {


        val gapWidth = 24.sp
        val lineOverflowIndent = gapWidth * 1.5
        val content = buildAnnotatedString {
            val bookEntries = songbookEntries()

            pushStyle(ParagraphStyle(textIndent = TextIndent(restLine = lineOverflowIndent)))
            pushStyle(SpanStyle(fontStyle = FontStyle.Italic, fontSize = 16.sp))
            for (entry in bookEntries) {
                append("${entry.songbook}, ${entry.entry}")
                appendLine()
            }
            appendLine()
            pop() // italic book entry
            pop() // end title text indent

            // Verses
            val lyricsByOrder = lyricsByVerseOrder()
            for (lyric in lyricsByOrder) {
                // start first line
                val allLines = lyric.content.split("\n")
                val firstLine = allLines.first()
                val restOfContent = allLines.drop(1)
                when (lyric.type) {
                    Lyric.Type.Verse -> {
                        pushStyle(ParagraphStyle(textIndent = TextIndent(restLine = lineOverflowIndent)))
                        append("${lyric.label?.substring(1)}.   $firstLine") // labels are in the form of v1, v2, etc. We just want the number
                        pop() // end first line


                        for (line in restOfContent) {
                            pushStyle(
                                ParagraphStyle(
                                    textIndent = TextIndent(
                                        firstLine = gapWidth,
                                        restLine = lineOverflowIndent
                                    )
                                )
                            ) // start line
                            append(line)
                            pop() // end line
                        }
                        appendLine()
                    }

                    Lyric.Type.Chorus, Lyric.Type.PreChorus -> {

                        for (line in allLines) {
                            pushStyle(SpanStyle(fontStyle = FontStyle.Italic))
                            pushStyle(
                                ParagraphStyle(
                                    textIndent = TextIndent(
                                        firstLine = gapWidth,
                                        restLine = lineOverflowIndent
                                    )
                                )
                            ) // start line
                            append(line)
                            pop() // end line
                            pop() // end font style
                        }
                        appendLine()
                    }

                    else -> {
                        for (line in allLines) {
                            pushStyle(
                                ParagraphStyle(
                                    textIndent = TextIndent(
                                        firstLine = gapWidth,
                                        restLine = lineOverflowIndent
                                    )
                                )
                            ) // start line
                            append(line)
                            pop() // end line
                        }
                        appendLine()
                    }
                }
            }

        }
        return SongUiDetail(
            title = AnnotatedString(title),
            content = content,
        )
    }
}
