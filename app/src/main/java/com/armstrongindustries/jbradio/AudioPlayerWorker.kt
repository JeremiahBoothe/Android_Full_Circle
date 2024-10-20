package com.armstrongindustries.jbradio

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.armstrongindustries.jbradio.service.AudioPlayerService

/**
 * Worker class for managing audio playback tasks.
 */
class AudioPlayerWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    override fun doWork(): Result {
        // Start the AudioPlayerService to handle playback
        val intent = Intent(applicationContext, AudioPlayerService::class.java).apply {
            action = ACTION_PLAY // Define an action for playback
        }
        applicationContext.startService(intent) // Start the service

        return Result.success() // Return success immediately
    }

    companion object {
        const val ACTION_PLAY = "com.armstrongindustries.jbradio.action.PLAY"
    }
}
