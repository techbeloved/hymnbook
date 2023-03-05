package com.techbeloved.hymnbook.utils.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.techbeloved.hymnbook.data.repo.HymnsRepository
import com.techbeloved.hymnbook.data.repo.OnlineRepo
import com.techbeloved.hymnbook.utils.SchedulerProvider
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.reactivex.Single

@HiltWorker
class HymnSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val schedulerProvider: SchedulerProvider,
    private val onlineRepo: OnlineRepo,
    private val hymnsRepository: HymnsRepository
) : RxWorker(context, params) {
    override fun createWork(): Single<Result> {
        return onlineRepo.getAllHymns()
            .flatMapCompletable { onlineHymns ->
                hymnsRepository.synchroniseOnlineMusic(onlineHymns)
            }
            .subscribeOn(schedulerProvider.io())
            .toSingleDefault(Result.retry())
    }

    companion object {
        fun create(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<HymnSyncWorker>().build()
        }
    }

}
