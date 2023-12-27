package com.techbeloved.hymnbook.shared.ui.home

import cafe.adriel.voyager.core.model.ScreenModel
import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.model.HymnItem
import com.techbeloved.hymnbook.shared.model.ext.OpenLyricsSong
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class HomeScreenModel : ScreenModel {
    val state: StateFlow<ImmutableList<HymnItem>> = MutableStateFlow(sampleHymnItems)

    companion object {
        val sampleSong = xmlDecoding()
        private val sampleHymnItems = persistentListOf(
            HymnItem(id = 1, title = "Hymn 1", subtitle = xmEncoding()),
            HymnItem(
                id = 2,
                title = sampleSong.properties.titles.first().value,
                subtitle = sampleSong.properties.titles[1].value,
            ),
            HymnItem(
                id = 3,
                title = "Hymn 3",
                subtitle = sampleSong.lyrics.first().lines.first().content.trimIndent()
            ),
            HymnItem(id = 4, title = "Hymn 4", subtitle = "Praise is good"),
            HymnItem(id = 5, title = "Hymn 5", subtitle = "Praise and worship"),
            HymnItem(id = 6, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 7, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 8, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 9, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 10, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 11, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 12, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 13, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 14, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 15, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 16, title = "Hymn 6", subtitle = "Praise in holiness"),
            HymnItem(id = 17, title = "Hymn 6", subtitle = "Praise in holiness"),
        )
    }
}

private val sampleOpenLyricsSongWithSongbook = OpenLyricsSong(
    createdIn = "Android Studio",
    properties = OpenLyricsSong.Properties(
        titles = listOf(
            OpenLyricsSong.Title("Song 1"),
            OpenLyricsSong.Title("Song of degrees"),
        ),
        songbooks = listOf(OpenLyricsSong.Songbook(name = "songbook1", entry = "1")),
        keywords = "Faith, believe, love, joy and peace",
        comments = listOf("I want to know him", "I love my master 1999"),
        authors = listOf(
            OpenLyricsSong.Author("John Newton", "lyrics", "published 1855"),
            OpenLyricsSong.Author("Edward Snowden", "music", "published 1999"),
        ),
        verseOrder = "v1 c1",
        themes = listOf(OpenLyricsSong.Theme("Worship")),
    ),
    lyrics = listOf(
        OpenLyricsSong.Verse(
            name = "v1",
            lines = listOf(OpenLyricsSong.Lines("Line 1\nLine 2")),
        ),
        OpenLyricsSong.Verse(
            name = "v2",
            lines = listOf(
                OpenLyricsSong.Lines("Line 3\nLine 4", part = "men"),
                OpenLyricsSong.Lines("Line 3\nLine 4", part = "women"),
            ),
        ),
    )
)

private fun xmEncoding(): String {

    return Injector.xml.encodeToString(OpenLyricsSong.serializer(), sampleOpenLyricsSongWithSongbook)
}

private fun xmlDecoding(): OpenLyricsSong =
    Injector.xml.decodeFromString(OpenLyricsSong.serializer(), SAMPLE_TWO.replace("<\\?xml.*\\?>".toRegex(), "").also(::println))

private const val SIMPLE_SONG = """
<song xmlns="http://openlyrics.info/namespace/2009/song"
      version="0.9">
  <properties>
    <titles>
      <title>Amazing Grace</title>
    </titles>
  </properties>
  <lyrics>
    <verse name="v1">
      <lines>
        Amazing grace how sweet the sound
      </lines>
    </verse>
  </lyrics>
</song>
"""
private const val SAMPLE_XML = """
    <?xml version='1.0' encoding='UTF-8'?>
<song xmlns="http://openlyrics.info/namespace/2009/song" version="0.8" createdIn="OpenLP 3.0.2" modifiedIn="OpenLP 3.0.2" modifiedDate="2023-03-12T11:13:57">
  <properties>
    <titles>
      <title>10,000 Reasons</title>
      <title>Bless The Lord, Bless the Lord o my soul</title>
    </titles>
    <authors>
      <author type="words">Jonas Myrin</author>
      <author type="music">Jonas Myrin</author>
      <author type="words">Matt Redman</author>
      <author type="music">Matt Redman</author>
    </authors>
    <songbooks>
      <songbook name="Rising Tabernakel"/>
    </songbooks>
    <themes>
      <theme>keyboard</theme>
      <theme>joy</theme>
      <theme>piano</theme>
      <theme>guitar</theme>
      <theme>praise</theme>
      <theme>meditation</theme>
      <theme>thanksgiving</theme>
      <theme>teaching</theme>
    </themes>
  </properties>
  <lyrics>
    <verse name="c1">
      <lines>Bless the <chord name="C"/>Lord, O my s<chord name="G"/>oul,<br/><chord name="D/F#"/>O my so<chord name="Em"/>ul,<br/><chord name="C"/>Worship His <chord name="G"/>holy <chord name="D"/>name<br/>Sing like <chord name="C"/>never bef<chord name="Em"/>ore,<br/><chord name="C"/>O <chord name="D"/>my s<chord name="Em"/>oul,<br/>I'll <chord name="C"/>worship Your <chord name="D"/>holy <chord name="G"/>name</lines>
    </verse>
    <verse name="v1">
      <lines>The <chord name="C"/>sun comes <chord name="G"/>up, it's a <chord name="D"/>new day d<chord name="Em"/>awning;<br/><chord name="C"/> It's time to <chord name="G"/>sing Your so<chord name="D"/>ng agai<chord name="Em"/>n<br/>What<chord name="C"/>ever may <chord name="G"/>pass, and what<chord name="D"/>ever lies be<chord name="Em"/>fore me,<br/><chord name="C2"/> Let me be s<chord name="G"/>inging when the e<chord name="Dsus4"/>ven <chord name="D"/>- ing <chord name="G"/>come<chord name="Gsus4"/>s.<chord name="G"/></lines>
    </verse>
    <verse name="v2">
      <lines>You're <chord name="C"/>rich in <chord name="G"/>love, and You're <chord name="D"/>slow to <chord name="Em"/>anger<br/>Your <chord name="C"/>name is <chord name="G"/>great, and Your <chord name="D"/>heart is ki<chord name="Em"/>nd<br/>For <chord name="C"/>all Your <chord name="G"/>goodness, I will <chord name="D"/>keep on s<chord name="Em"/>inging;<br/><chord name="C2"/> Ten thousand <chord name="G"/>reasons for my <chord name="Dsus4"/>heart <chord name="D"/>to <chord name="G"/>find<chord name="Gsus4 G"/></lines>
    </verse>
    <verse name="v3">
      <lines>And <chord name="C"/>on that <chord name="G"/>day when my <chord name="D"/>strength is f<chord name="Em"/>ailing,<br/>The <chord name="C"/>end draws <chord name="G"/>near, and my <chord name="D"/>time has co<chord name="Em"/>me;<br/><chord name="C"/>Still my <chord name="G"/>soul will sing Your <chord name="D"/>praise un<chord name="Em"/>ending;<br/><chord name="C2"/> Ten thousand <chord name="G"/>years and then for<chord name="Dsus4"/>ever<chord name="D"/>mo<chord name="G"/>r<chord name="Gsus4"/>e!<chord name="G"/></lines>
    </verse>
  </lyrics>
</song>
"""

private const val SAMPLE_TWO = """
<?xml version="1.0" ?>
<song chordNotation="english" createdIn="Android Studio" lang="en" version="0.9">
    <properties keywords="Faith, believe, love, joy and peace" verseOrder="v1 c1">
        <titles>
            <title>Song 1</title>
            <title>Song of degrees</title>
        </titles>
        <authors>
            <author name="John Newton" comment="published 1855" type="lyrics" />
            <author name="Edward Snowden" comment="published 1999" type="music" />
        </authors>
        <songbooks>
            <songbook name="songbook1" entry="1" />
        </songbooks>
        <themes>
            <theme>Worship</theme>
        </themes>
        <comments>
            <comment>I want to know him</comment>
            <comment>I love my master 1999</comment>
        </comments>
    </properties>
    <lyrics>
        <verse name="v1">
            <lines>
            Line 1
            Line 2
            </lines>
        </verse>
        <verse name="v2">
            <lines part="men">
            Line 3
            Line 4
            </lines>
            <lines part="women">
            Line 3
            Line 4
            </lines>
        </verse>
    </lyrics>
</song>
"""
