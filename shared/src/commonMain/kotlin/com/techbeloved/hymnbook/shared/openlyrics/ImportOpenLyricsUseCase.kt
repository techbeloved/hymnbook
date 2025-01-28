package com.techbeloved.hymnbook.shared.openlyrics

import com.techbeloved.hymnbook.shared.di.Injector
import com.techbeloved.hymnbook.shared.dispatcher.DispatchersProvider
import com.techbeloved.hymnbook.shared.files.OkioFileSystemProvider
import com.techbeloved.hymnbook.shared.model.ext.OpenLyricsSong
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import nl.adaptivity.xmlutil.QName
import nl.adaptivity.xmlutil.serialization.XML
import okio.Path
import okio.buffer
import okio.use

internal class ImportOpenLyricsUseCase @Inject constructor(
    private val fileSystemProvider: OkioFileSystemProvider,
    private val dispatchersProvider: DispatchersProvider,
    private val saveOpenLyricsUseCase: SaveOpenLyricsUseCase,
    private val xml: XML = Injector.xml,
) {
    suspend operator fun invoke(directory: Path) = withContext(dispatchersProvider.io()) {
        val fileSystem = fileSystemProvider.get().fileSystem
        runCatching {
            fileSystem.listRecursively(directory)
                .filter { it.name.endsWith(".xml") }
                .forEach { lyricsPath ->
                    val lyricsXmlContent = fileSystem.source(lyricsPath).use { fileSource ->
                        fileSource.buffer().use { bufferedSource ->
                            bufferedSource.readUtf8()
                        }
                    }
                    runCatching {
                        val openLyricsSong = xml.decodeFromString(
                            deserializer = OpenLyricsSong.serializer(),
                            string = lyricsXmlContent
                                .replace("<br/>", "\n"),
                            rootName = QName("http://openlyrics.info/namespace/2009/song", "song"),
                        )
                        saveOpenLyricsUseCase(openLyricsSong)
                    }
                }
        }
    }
}
