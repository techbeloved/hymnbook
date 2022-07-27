package com.techbeloved.hymnbook.data.repo

import com.google.firebase.firestore.FirebaseFirestore
import com.techbeloved.hymnbook.utils.WCCRM_HYMNS_COLLECTION
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
interface HymnRepoModule {

    @Binds
    fun bindHymnRepository(hymnsRepositoryImp: HymnsRepositoryImp): HymnsRepository

    @Binds
    fun bindOnlineRepo(firebaseRepo: FirebaseRepo): OnlineRepo


    companion object {
        @Provides
        fun provideFirebaseFireStore(): FirebaseFirestore = FirebaseFirestore.getInstance()

        @Provides
        @Named("FS_COLLECTION")
        fun provideFirestoreCollection() = WCCRM_HYMNS_COLLECTION
    }
}