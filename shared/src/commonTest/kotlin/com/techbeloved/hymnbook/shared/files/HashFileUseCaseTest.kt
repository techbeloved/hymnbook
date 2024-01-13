package com.techbeloved.hymnbook.shared.files

import com.techbeloved.hymnbook.shared.model.file.FileHash
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class HashFileUseCaseTest {

    private val scope = TestScope()
    private val fakeFileSystem = FakeFileSystem()
    private val useCase = HashAssetFileUseCase(defaultAssetFileProvider = {
        fakeFileSystem.source(it.toPath())
    })

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher(scope.testScheduler))
    }

    @AfterTest
    fun tearDown() {
        fakeFileSystem.checkNoOpenFiles()
        Dispatchers.resetMain()
    }

    @Test
    fun hashing() = runTest {
        val assetsDirectory = "/resources/assets".toPath()
        val sampleFile = assetsDirectory / "sample_file"
        val sampleContent = "email@email.com"
        val sampleHasValue = "f3273dd18d95bc19d51d3e6356e4a679e6f13824497272a270e7bb540b0abb9d"
        fakeFileSystem.createDirectories(assetsDirectory)
        fakeFileSystem.write(sampleFile) { writeUtf8(sampleContent) }

        val result = useCase(sampleFile.toString())
        assertEquals(FileHash(sampleFile.toString(), sampleHasValue), result)
    }
}
