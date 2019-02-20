package com.techbeloved.hymnbook.hymndetail

import com.techbeloved.hymnbook.data.model.Hymn
import com.techbeloved.hymnbook.data.model.HymnDetail
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

class HymnDetailItemKtTest {

    @Test
    fun hymnDetailToHtmlContent_should_generate_a_valid_html_document() {
        val detailHymn = HymnDetail(
                "hymn_1", 1, "hymn1",
                listOf("verse1", "verse2", "verse3"),
                "topic1")
        detailHymn.chorus = "chorus1"
        val attribution = Hymn.Attribution()
        attribution.musicBy = "Jim"
        attribution.lyricsBy = "Will"

        detailHymn.attribution = attribution

        val expected = """
            <!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Hymns</title>
    <link rel="stylesheet" href="style.css">
</head>

<body>
    <header>
        <h2 class="hymn_no">1</h2>
        <h2 class="title">hymn1</h2>
    </header>
    <ol><li><span>verse1</span><p class="chorus"><span>chorus1</span></p></li>
    <li><span>verse2</span><p class="chorus"><span>chorus1</span></p></li>
    <li><span>verse3</span><p class="chorus"><span>chorus1</span></p></li>
    </ol>
    <footer><p><span class="footer-header">Lyrics by: </span> <span class="lyrics">Will</span></p><p><span class="footer-header">Music by: </span><span class="music">Jim</span></p></footer>
 </body>
 </html>
        """.trimIndent().replace("\n", "").replace(" ", "")
        // Spaces can give us unreliable results, so remove them

        // Execute
        val result = detailHymn.htmlContent.replace(" ", "")

        assertThat(result, `is`(equalTo(expected)))

    }

}