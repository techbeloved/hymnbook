package com.techbeloved.hymnbook.data.repo

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.firestore.FirebaseFirestore
import com.techbeloved.hymnbook.di.WCCRM_HYMNS_COLLECTION
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
class FirebaseRepoTest {

    private lateinit var subject: FirebaseRepo
    private lateinit var fireStore: FirebaseFirestore
    private lateinit var executor: ExecutorService
    private val defaultCollection: String = WCCRM_HYMNS_COLLECTION

    // endregion helper fields ---------------------------------------------------------------------

    @Before
    fun setUp() {
        fireStore = FirebaseFirestore.getInstance()
        executor = Executors.newSingleThreadExecutor()
        subject = FirebaseRepo(executor, fireStore, defaultCollection)


    }

    @Test
    fun latestMidiArchive_success_returnsTheLatestHymnMidiArchiveInfo() {
        // Setup
        val testObserver = TestObserver<OnlineMidi>()

        // Execute
        subject.latestMidiArchive().subscribe(testObserver)

        testObserver.await()
        // Verify
        testObserver.assertSubscribed()
        testObserver.assertNoErrors()
        testObserver.assertValue { midi -> midi.version > 0 }

    }


}