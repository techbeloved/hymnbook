package com.techbeloved.hymnbook.data.repo

import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Observable
import io.reactivex.Observable.create
import java.util.concurrent.ExecutorService


class FirebaseRepo (private val executor: ExecutorService,
                                       private val firestore: FirebaseFirestore,
                                       private val collection: String) : OnlineRepo {

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
                executor.shutdown()
                successRegistration.remove()
            }

        }
    }

}