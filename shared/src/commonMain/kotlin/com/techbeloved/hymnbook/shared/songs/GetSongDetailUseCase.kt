package com.techbeloved.hymnbook.shared.songs

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.techbeloved.hymnbook.Database
import com.techbeloved.hymnbook.SongDetail
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.model.Lyric
import com.techbeloved.hymnbook.shared.model.ext.lyricsByVerseOrder
import com.techbeloved.hymnbook.shared.model.ext.songbookEntries
import com.techbeloved.hymnbook.shared.ui.detail.SongUiDetail
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject

private const val IndentFactor = 1.5

internal class GetSongDetailUseCase @Inject constructor(
    private val database: Database,
    private val dispatchersProvider: DispatchersProvider,
) {

    suspend operator fun invoke(songId: Long): SongUiDetail =
        withContext(dispatchersProvider.io()) {
            database.songEntityQueries.getSongById(songId).executeAsOne()
                .toUiDetail()
        }

    private fun SongDetail.toUiDetail(): SongUiDetail {
        val gapWidth = 24.sp
        val lineOverflowIndent = gapWidth * IndentFactor
        val content = buildAnnotatedString {
            val bookEntries = songbookEntries()

            pushStyle(SpanStyle(fontSize = 24.sp))
            append(title.trim())
            pop() // end title line

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
                        appendLyricLines(
                            lineOverflowIndent = lineOverflowIndent,
                            lyric = lyric,
                            firstLine = firstLine,
                            restOfContent = restOfContent,
                            gapWidth = gapWidth,
                        )
                        appendLine()
                    }

                    Lyric.Type.Chorus, Lyric.Type.PreChorus -> {
                        appendChorus(
                            allLines = allLines,
                            gapWidth = gapWidth,
                            lineOverflowIndent = lineOverflowIndent,
                        )
                        appendLine()
                    }

                    else -> {
                        appendOthers(
                            allLines = allLines,
                            gapWidth = gapWidth,
                            lineOverflowIndent = lineOverflowIndent,
                        )
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

    private fun AnnotatedString.Builder.appendOthers(
        allLines: List<String>,
        gapWidth: TextUnit,
        lineOverflowIndent: TextUnit,
    ) {
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
    }

    private fun AnnotatedString.Builder.appendChorus(
        allLines: List<String>,
        gapWidth: TextUnit,
        lineOverflowIndent: TextUnit,
    ) {
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
    }

    private fun AnnotatedString.Builder.appendLyricLines(
        lineOverflowIndent: TextUnit,
        lyric: Lyric,
        firstLine: String,
        restOfContent: List<String>,
        gapWidth: TextUnit,
    ) {
        pushStyle(ParagraphStyle(textIndent = TextIndent(restLine = lineOverflowIndent)))
        // labels are in the form of v1, v2, etc. We just want the number
        append("${lyric.label?.substring(1)}.   $firstLine")
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
    }
}
