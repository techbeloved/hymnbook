package com.techbeloved.hymnbook.hymndetail

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextUtils
import com.techbeloved.hymnbook.data.model.HymnDetail
import java.lang.StringBuilder

data class HymnDetailItem(val num: Int, val title: String, val subtitle: String, val content: CharSequence)

const val baseHtml = """<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Hymns</title>
    <link rel="stylesheet" href="style.css">
</head>

<body>
    <header>
        <h2 class="hymn_no">{num}</h2>
        <h2 class="title">{title}</h2>
    </header>
    <ol>{content}</ol>
    <footer>{footer}</footer>
 </body>
 </html>
 """

const val footerLyricsBy = """
<p><span class="footer-header">Lyrics by: </span> <span class="lyrics">{lyrics}</span> </p>
<p><span class="footer-header">Music by: </span><span class="music">{music}</span></p>
"""

const val verseTemplate = """

"""

const val creditsTo = """<p><span class="footer-header">Credits: </span><span class="music">{credits}</span></p>"""

val HymnDetail.htmlContent: String
    get() {
        val verseBuilder = StringBuilder()
        val spannedChorus = this.chorus?.let { """<p class="chorus">${it.spannedLines}</p>""" }
        for (verse in this.verses) {
            verseBuilder.append("<li>${verse.spannedLines} ${spannedChorus ?: ""}</li>")
        }
        var attribution: String? = null
        if (this.attribution != null) {
            if (this.attribution!!.lyricsBy != null) {
                attribution = footerLyricsBy
                        .replace("{lyrics}", "${this.attribution?.lyricsBy}")
                        .replace("{music}", "${this.attribution?.musicBy}")
            } else if (this.attribution!!.credits != null) {
                attribution = creditsTo.replace("{credits}", "${this.attribution?.credits}")
            }
        }

        return baseHtml.replace("{content}", verseBuilder.toString())
                .replace("{num}", this.num.toString())
                .replace("{title}", this.title)
                .replace("{footer}", attribution ?: "")
                .trimIndent()
                .replace("\n", "")
    }


private val String.spannedLines: String
    get() {
        val lines = this.split("\n")
        val spanBuilder = StringBuilder()
        for (line in lines) {
            spanBuilder.append("<span>$line</span>")
        }
        return spanBuilder.toString()
    }


private fun firstLine(verse: String): String {
    return verse.substring(0, verse.indexOf("\n"))
}



/*inline fun SpannableStringBuilder.withSpan(span: Any, action: SpannableStringBuilder.() -> Unit): SpannableStringBuilder {
    val from = length
    action()
    setSpan(span, from, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return this
}*/




