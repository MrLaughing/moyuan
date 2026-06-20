package com.mrlaughing.moyuan.sync

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.mrlaughing.moyuan.util.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val workManager = WorkManager.getInstance(context)

    fun scheduleDailySync() {
        val request = PeriodicWorkRequestBuilder<SyncWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .addTag(Constants.SYNC_WORK_NAME)
            .build()
        workManager.enqueueUniquePeriodicWork(
            Constants.SYNC_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    fun triggerNow(): LiveData<WorkInfo> {
        val request = OneTimeWorkRequestBuilder<SyncWorker>()
            .addTag(Constants.SYNC_WORK_NAME)
            .build()
        workManager.enqueueUniqueWork(
            Constants.SYNC_WORK_NAME + "_now",
            ExistingWorkPolicy.REPLACE,
            request
        )
        return workManager.getWorkInfoByIdLiveData(request.id)
    }

    fun cancelAll() = workManager.cancelAllWorkByTag(Constants.SYNC_WORK_NAME)
}
