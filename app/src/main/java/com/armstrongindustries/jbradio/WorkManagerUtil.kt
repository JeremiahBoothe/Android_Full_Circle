package com.armstrongindustries.jbradio

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

/**
 *
 */
class WorkManagerUtil(private val context: Context) {

    fun scheduleMetadataFetching() {
        val workRequest = PeriodicWorkRequestBuilder<AudioPlayerWorker>(15, TimeUnit.MINUTES)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(
                "AudioPlayerMetadata",
                ExistingPeriodicWorkPolicy.REPLACE,
                workRequest)
    }
}
