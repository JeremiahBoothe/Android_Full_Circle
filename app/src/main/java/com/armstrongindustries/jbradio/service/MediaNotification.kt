package com.armstrongindustries.jbradio.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.media3.session.MediaStyleNotificationHelper
import com.armstrongindustries.jbradio.R
import com.armstrongindustries.jbradio.data.Constants
import com.armstrongindustries.jbradio.repository.Repository

/**
 * Class responsible for managing the playback notification.
 * @property service the AudioPlayerService instance.
 * @property repository the Repository instance.
 */
class MediaNotification(private val service: AudioPlayerService) {
    private val repository: Repository = Repository.getInstance(service.application)

    init {
        createNotificationChannel() // Create channel during initialization
    }

    private fun createNotificationChannel() {
        val notificationManager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = Constants.PLAYBACK_CHANNEL_ID
        if (notificationManager.getNotificationChannel(channelId) == null) {
            val channelName = service.getString(R.string.playback_channel_name)
            val channelDescription = service.getString(R.string.playback_channel_description)
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW).apply {
                description = channelDescription
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun updateNotification(
        songTitle: String?,
        songDescription: String?,
        songIcon: Int?
    ) {
        val mediaSessionToken = service.getMediaSession().platformToken
        val notification = NotificationCompat.Builder(service, Constants.PLAYBACK_CHANNEL_ID)
            .setContentTitle(songTitle ?: "Unknown Title")
            .setContentText(songDescription ?: "Unknown Description")
            .setLargeIcon(Constants.getBitmap(service, songIcon ?: R.drawable.sle_radio))
            .setSmallIcon(R.drawable.ic_notifications_black_24dp) // Use a drawable resource ID for the small icon
            .setContentIntent(createPendingIntent())
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                MediaStyleNotificationHelper.MediaStyle(service.getMediaSession())
                    .setShowActionsInCompactView(0, 1, 2) // Show play, pause, skip actions
            )
            .addAction(createAction(R.string.action_play, Constants.ACTION_PLAY))
            .addAction(createAction(R.string.action_pause, Constants.ACTION_PAUSE))
            .addAction(createAction(R.string.action_next, Constants.ACTION_NEXT))
            .build()

        service.startForeground(Constants.PLAYBACK_NOTIFICATION_ID, notification)
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(service, AudioPlayerService::class.java) // Adjust if necessary
        return PendingIntent.getActivity(service, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    private fun createAction(label: Int, action: String): NotificationCompat.Action {
        val intent = Intent(action).apply {
            setPackage(service.packageName)
        }
        val pendingIntent = PendingIntent.getBroadcast(service, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        return NotificationCompat.Action.Builder(0, service.getString(label), pendingIntent).build()
    }
}
