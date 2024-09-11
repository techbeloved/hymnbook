package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.model.file.FileHash
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HashFileUseCaseTest {

    private val fakeFileSystem = FakeFileSystem()
    private val useCase = HashAssetFileUseCase(
        defaultAssetFileSourceProvider = {
            fakeFileSystem.source(it.toPath())
        })

    @AfterTest
    fun tearDown() {
        fakeFileSystem.checkNoOpenFiles()
    }

    @Test
    fun hashing() = runTest {
        val assetsDirectory = "/composeResources/assets".toPath()
        val sampleFile = assetsDirectory / "sample_file"
        val sampleContent = "email@email.com"
        val sampleHasValue = "f3273dd18d95bc19d51d3e6356e4a679e6f13824497272a270e7bb540b0abb9d"
        fakeFileSystem.createDirectories(assetsDirectory)
        fakeFileSystem.write(sampleFile) { writeUtf8(sampleContent) }

        val result = useCase(sampleFile.toString())
        assertEquals(FileHash(sampleFile.toString(), sampleHasValue), result)
    }
}
