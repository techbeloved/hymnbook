package com.techbeloved.hymnbook.data.repo

import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.techbeloved.hymnbook.hymndetail.BY_NUMBER
import com.techbeloved.hymnbook.hymndetail.BY_TITLE
import com.techbeloved.hymnbook.hymndetail.SortBy
import io.reactivex.Observable
import io.reactivex.Observable.create
import timber.log.Timber
import java.util.concurrent.Executor
import javax.inject.Inject
import javax.inject.Named


class FirebaseRepo @Inject constructor(@Named("IO") private val executor: Executor,
                                       private val firestore: FirebaseFirestore,
                                       @Named("FS_COLLECTION") private val collection: String) : OnlineRepo {

    override fun hymnIds(@SortBy orderBy: Int): Observable<List<Int>> {
        val orderCriteria = when (orderBy) {
            BY_NUMBER -> "num"
            BY_TITLE -> "title"
            else -> "num"
        }
        return create { emitter ->
            val hymnCollectionRef = firestore.collection(collection).orderBy(orderCriteria)
            val successRegistration = hymnCollectionRef.addSnapshotListener(executor, EventListener { querySnapshot, firestoreException ->
                if (firestoreException == null) {
                    val hymnIds = mutableListOf<Int>()
                    querySnapshot?.documents?.map { documentSnapshot ->
                        hymnIds.add((documentSnapshot.get("num") as Long).toInt())
                    }
                    emitter.onNext(hymnIds)
                } else {
                    emitter.tryOnError(Throwable("Error getting songs from firestore!", firestoreException))
                }
            })

            emitter.setCancellable {
                successRegistration.remove()
            }

        }
    }


    override fun getAllHymns(): Observable<List<OnlineHymn>> {
        return create { emitter ->

            val hymnCollectionRef = firestore.collection(collection).orderBy("num")
            val successRegistration = hymnCollectionRef.addSnapshotListener(executor, EventListener { querySnapshot, firestoreException ->
                if (firestoreException == null) {
                    val hymns = mutableListOf<OnlineHymn>()
                    querySnapshot?.documents?.map { documentSnapshot ->
                        hymns.add(
                                OnlineHymn(
                                        (documentSnapshot.get("num") as Long).toInt(),
                                        documentSnapshot["title"] as String
                                )
                        )
                    }
                    emitter.onNext(hymns)
                } else {
                    emitter.tryOnError(Throwable("Error getting songs from firestore!", firestoreException))
                }
            })


            emitter.setCancellable {
                successRegistration.remove()
            }

        }
    }

    override fun getHymnById(id: Int): Observable<OnlineHymn> {
        return create { emitter ->
            val hymnDocumentRef = firestore.collection(collection).document("hymn_$id")
            val successRegistration = hymnDocumentRef.addSnapshotListener(executor, EventListener { documentSnapshot, firestoreException ->
                if (firestoreException == null && documentSnapshot != null) {

                    val hymn = OnlineHymn(
                            (documentSnapshot.get("num") as Long).toInt(),
                            documentSnapshot.get("title") as String,
                            sheetMusicUrl = documentSnapshot["sheet_music"] as String
                    )
                    emitter.onNext(hymn)
                } else {
                    emitter.tryOnError(Throwable("Error getting song from firestore!", firestoreException))
                }
            })


            emitter.setCancellable {
                successRegistration.remove()
            }
        }
    }

    override fun getLatestCatalogUrl(): Observable<String> {
        return create { emitter ->
            val catalogDocumentRef = firestore.collection("catalog")
                    .orderBy("version", Query.Direction.DESCENDING)
            val successRegistration = catalogDocumentRef.addSnapshotListener { querySnapshot, firestoreException ->
                if (firestoreException == null && querySnapshot != null) {
                    val latest = querySnapshot.documents.first { doc -> doc.id.contains("wccrm") }
                    val docUrl = latest?.get("url") as String?
                    if (!emitter.isDisposed) {
                        docUrl?.let { emitter.onNext(it) }
                    }
                } else {
                    emitter.tryOnError(Throwable("Error getting catalog!", firestoreException))
                }
            }

            emitter.setCancellable { successRegistration.remove() }
        }
    }

    override fun latestMidiArchive(): Observable<OnlineMidi> {
        return create { emitter ->
            val midiArchiveRef = firestore.document(WCCRM_MIDI_ARCHIVE_DOC_REF)
            val documentRegistration = midiArchiveRef.addSnapshotListener { snapshot, exception ->
                if (exception == null && snapshot != null) {
                    val midi = snapshot.toObject(OnlineMidi::class.java)
                    Timber.i("Got something: %s", midi)
                    if (!emitter.isDisposed) {
                        midi?.let { emitter.onNext(it) }
                        emitter.onComplete()
                    }
                } else if (snapshot == null) {
                    emitter.tryOnError(Throwable("Nothing received from firebase! snapshot: $snapshot"))
                } else {
                    emitter.tryOnError(Throwable("Error getting midi archive!", exception))
                }
            }
            emitter.setCancellable { documentRegistration.remove() }

        }
    }

}

const val WCCRM_MIDI_ARCHIVE_DOC_REF = "tunes/wccrm_midi"