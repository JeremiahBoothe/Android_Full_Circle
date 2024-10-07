package com.armstrongindustries.jbradio.ui.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import com.armstrongindustries.jbradio.R

/**
 * Class responsible for managing the playback notification.
 * @param service the AudioPlayerService instance.
 * @author Jeremiah Boothe
 * @date 06/24/2024
 * @see AudioPlayerService
 * @see Notification
 * @see NotificationManager
 * @see NotificationChannel
 * @see PendingIntent
 */
class MediaNotification(private val service: AudioPlayerService) {

    fun createNotificationChannel() {
        val notificationManager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = Constants.PLAYBACK_CHANNEL_ID
        if (notificationManager.getNotificationChannel(channelId) != null) return

        val channelName = service.getString(R.string.playback_channel_name)
        val channelDescription = service.getString(R.string.playback_channel_description)
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }
        notificationManager.createNotificationChannel(channel)
    }

    @OptIn(UnstableApi::class)
    fun updateNotification(
        songTitle: String?,
        songDescription: String?,
        songIcon: Int?,
    ) {
        val mediaSessionToken = service.mediaSession.platformToken
        val notification = Notification.Builder(service, Constants.PLAYBACK_CHANNEL_ID)
            .setContentTitle(songTitle ?: "Unknown Title")
            .setContentText(songDescription ?: "Unknown Description")
            .setLargeIcon(Constants.getBitmap(service, songIcon ?: R.drawable.sle_radio))
            .setSmallIcon(R.drawable.ic_notifications_black_24dp) // Use a drawable resource ID for the small icon
            .setContentIntent(PendingIntent.getActivity(service, 0, Intent(service,
                AudioPlayerService::class.java), PendingIntent.FLAG_IMMUTABLE))
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setStyle(
                Notification.MediaStyle()
                    .setShowActionsInCompactView(1) // Show the pause button in compact view
                    .setMediaSession(mediaSessionToken)
            )
            .build()

        service.startForeground(Constants.PLAYBACK_NOTIFICATION_ID, notification)
    }


    private fun createPendingIntent(action: String): PendingIntent {
        val intent = Intent(action).apply {
            setPackage(service.packageName)
        }
        return PendingIntent.getBroadcast(service, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
}