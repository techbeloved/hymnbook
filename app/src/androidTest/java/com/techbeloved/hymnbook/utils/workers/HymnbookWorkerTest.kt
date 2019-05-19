package com.techbeloved.hymnbook.utils.workers

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.content.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.techbeloved.hymnbook.data.repo.OnlineMidi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class HymnbookWorkerTest {

    private val midiVersionKey = "midiArchiveVersion"
    private val testPreferencesName = "test_preferences"

    private val onlineMidi = OnlineMidi(id = "wccrm_midi",
            url = "https://storage.googleapis.com/hymnbook-50b7e.appspot.com/tunes/wccrm/midi_archive/hymns.zip",
            version = 3)


    private lateinit var midiPrefSyncInputData: Data

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    var wmRule = WorkManagerTestRule()

    @Before
    fun setUp() {

        midiPrefSyncInputData = Data.Builder()
                .putString(KEY_DEFAULT_PREFERENCE_NAME, testPreferencesName)
                .putString(KEY_PREF_MIDI_VERSION, midiVersionKey)
                .build()

    }


    @After
    fun tearDown() {
        clearMidiVersionInSharedPreferences()
    }


    @Test
    fun midiSyncVersionCheck_success_onlineVersionDiffersFromLocalVersion() {

        val onlineMidi = OnlineMidi(id = "wccrm_midi",
                url = "https://storage.googleapis.com/hymnbook-50b7e.appspot.com/tunes/wccrm/midi_archive/hymns.zip",
                version = 3)
        val outPutData = Data.Builder().putString(KEY_FIREBASE_ARCHIVE_PATH, onlineMidi.url)
                .build()

        val worker = TestListenableWorkerBuilder<MidiSyncWorker>(wmRule.testContext, midiPrefSyncInputData)
                .build()

        val result = worker.startWork().get()

        assertThat(result, `is`(ListenableWorker.Result.success(outPutData)))
    }

    @Test
    fun midiSyncVersionCheck_failure_onlineVersionSameWithLocalVersion() {
        setMidiVersionInSharedPreferences(3)

        val worker = TestListenableWorkerBuilder<MidiSyncWorker>(wmRule.testContext, midiPrefSyncInputData)
                .build()

        val result = worker.startWork().get()

        assertThat(result, `is`(ListenableWorker.Result.failure()))
    }

    @Test
    fun downloadFirebaseArchive() {
        val destinationDir = File(wmRule.targetContext.getExternalFilesDir(null), "temp")
        destinationDir.mkdir()
        val destination = File(destinationDir, "hymns.zip")
        val archiveDestination = destination.absolutePath
        val archivePath = onlineMidi.url.substring(onlineMidi.url.lastIndexOf("tunes"))

        val inputData = workDataOf(
                KEY_FIREBASE_ARCHIVE_PATH to archivePath,
                KEY_ARCHIVE_DESTINATION to archiveDestination
        )
        val expected = workDataOf(KEY_DOWNLOADED_ARCHIVE to destination.absolutePath)

        val worker =
                TestListenableWorkerBuilder<DownloadFirebaseArchiveWorker>(wmRule.targetContext, inputData)
                        .build()

        val result = worker.startWork().get()

        assertThat(result, `is`(ListenableWorker.Result.success(expected)))
        destinationDir.delete()
        destination.delete()
    }

    private fun setMidiVersionInSharedPreferences(version: Int) {
        val midiVersionPref = wmRule.testContext.getSharedPreferences(midiVersionKey, Context.MODE_PRIVATE)
        midiVersionPref.edit(commit = true) {
            putInt(midiVersionKey, version)
        }

    }

    private fun clearMidiVersionInSharedPreferences() {
        val midiVersionPref = wmRule.testContext.getSharedPreferences(midiVersionKey, Context.MODE_PRIVATE)
        midiVersionPref.edit(commit = true) {
            clear()
        }
    }
}