package com.techbeloved.hymnbook.shared.ui.utils

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.TextUnit
import com.techbeloved.hymnbook.shared.model.Lyric
import com.techbeloved.hymnbook.shared.songs.SongData

@Composable
internal fun SongData.toUiDetail(fontSize: TextUnit): AnnotatedString {
    val textStyle = LocalTextStyle.current
    val localDensity = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val bulletSize = remember(fontSize, textStyle, textMeasurer) {
        textMeasurer.measure("9.\t\t", textStyle).size.width
    }
    val lineOverflow = remember(bulletSize, fontSize, textStyle, textMeasurer) {
        textMeasurer.measure("\t\t\t", textStyle).size.width + bulletSize
    }
    val bulletWidth = with(localDensity) {
        bulletSize.toSp()
    }
    val lineOverflowIndent = with(localDensity) {
        lineOverflow.toSp()
    }

    val content = buildAnnotatedString {

        appendLine()
        pushStyle(ParagraphStyle(textAlign = TextAlign.Center))
        pushStyle(SpanStyle(fontSize = fontSize * 1.15, fontWeight = FontWeight.ExtraBold))

        appendLine(title.trim())

        authors.firstOrNull()?.let { author ->
            pushStyle(SpanStyle(fontStyle = FontStyle.Normal, fontWeight = FontWeight.Light, fontSize = fontSize * .75f))
            appendLine(author.name)
            pop()
        }
        pop() // bold title
        pop() // end centred paragraph

        // Verses

        for (lyric in lyrics) {
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
                        gapWidth = bulletWidth,
                    )
                    append(' ')
                }

                Lyric.Type.Chorus, Lyric.Type.PreChorus -> {
                    appendChorus(
                        allLines = allLines,
                        gapWidth = bulletWidth,
                        lineOverflowIndent = lineOverflowIndent,
                    )
                    append(' ')
                }

                else -> {
                    appendOthers(
                        allLines = allLines,
                        gapWidth = bulletWidth,
                        lineOverflowIndent = lineOverflowIndent,
                    )
                    append(' ')
                }
            }
        }
        appendLine()
    }
    return content
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
    pushStyle(
        ParagraphStyle(
            textIndent = TextIndent(
                restLine = lineOverflowIndent,
            ),
        )
    )
    // labels are in the form of v1, v2, etc. We just want the number

    append("${lyric.label?.substring(1)}.\t\t")
    append(firstLine)

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

