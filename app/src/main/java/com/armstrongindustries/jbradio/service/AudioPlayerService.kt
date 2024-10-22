package com.armstrongindustries.jbradio.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.provider.Settings
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.*
import androidx.media3.common.C.SPATIALIZATION_BEHAVIOR_AUTO
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.armstrongindustries.jbradio.data.Constants
import com.armstrongindustries.jbradio.data.Repository
import com.google.android.material.R
import com.google.common.util.concurrent.ListenableFuture

/**
 * Forgeground Service to allow the audio player to be played in the background uninterrupted.
 */
class AudioPlayerService : MediaSessionService(),
    Player.Listener,
    MediaController.Listener {
    private lateinit var repository: Repository
    private var exoPlayer: ExoPlayer? = null

    private val idMetaData = MutableLiveData<Int>()
    private val artistMetaData = MutableLiveData<String>()
    private val songTitleLiveData = MutableLiveData<String>()
    private val albumTitleMetaData = MutableLiveData<String>()
    private val artworkMetaData = MutableLiveData<String>()

    lateinit var mediaSession: MediaSession
    private val binder = AudioPlayerServiceBinder()
    private lateinit var mediaNotification: MediaNotification

    override fun onCreate() {
        super.onCreate()
        repository = Repository.getInstance(application)
        mediaNotification = MediaNotification(this)
        if (isNotificationPermissionGranted()) {
            mediaNotification.createNotificationChannel()
            initializeExoPlayer()
            initializeMediaSession()
        } else {
            requestNotificationPermission()
        }
        mediaNotification.updateNotification(
            songTitleLiveData.value,
            artistMetaData.value,
            idMetaData.value
        )
    }

    private fun requestNotificationPermission() {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }

    private fun isNotificationPermissionGranted(): Boolean {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.areNotificationsEnabled()
    }


    @OptIn(UnstableApi::class)
    private fun initializeExoPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build().apply {
            setHandleAudioBecomingNoisy(true)
            setWakeMode(C.WAKE_MODE_NETWORK)
            setTheme(R.style.Theme_Material3_Dark)
            setForegroundMode(true)
            addListener(this@AudioPlayerService)
            setMediaSource(buildMediaSource())
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setSpatializationBehavior(SPATIALIZATION_BEHAVIOR_AUTO)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(C.USAGE_MEDIA)
                    .build(),
                true
            )
            prepare()
            playWhenReady = true
        }
        exoPlayer.let {
            theme.applyStyle(android.R.style.Theme_Material, true)
        }
    }

    @OptIn(UnstableApi::class)
    private fun initializeMediaSession() {
        if (!::mediaSession.isInitialized) {
            mediaSession = MediaSession.Builder(this, exoPlayer!!)
                .setCallback(object : MediaSession.Callback {
                    override fun onPlaybackResumption(
                        mediaSession: MediaSession,
                        controller: MediaSession.ControllerInfo,
                    ): ListenableFuture<MediaSession.MediaItemsWithStartPosition> {
                        return super.onPlaybackResumption(mediaSession, controller)
                    }
                    override fun onConnect(
                        session: MediaSession,
                        controller: MediaSession.ControllerInfo,
                    ): MediaSession.ConnectionResult {
                        return super.onConnect(session, controller)
                    }

                })
                .build()
        }
        // Set the session activity here
        mediaSession.setSessionActivity(
            PendingIntent
                .getActivity(
                this,
                0,
                Intent(this, AudioPlayerService::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )
        )
        updateNotification(
            binder.getTitleLiveData().value,
            binder.getArtistMetaData().value,
            binder.getIdLiveData().value
        )
    }

    private fun updateNotification(songTitle: String?,
                                   songDescription: String?,
                                   songIcon: Int?) {
        mediaNotification.updateNotification(
            songTitle,
            songDescription,
            songIcon
        )
    }

    override fun onDestroy() {
        exoPlayer?.release()
        mediaSession.release()
        super.onDestroy()
    }

    inner class AudioPlayerServiceBinder : Binder() {
        fun getExoPlayerInstance() = exoPlayer
        fun setExoPlayer(player: ExoPlayer) {
            exoPlayer = player
            initializeMediaSession() // Initialize the media session after setting the player
        }
        fun getIdLiveData(): LiveData<Int> = idMetaData
        fun getTitleLiveData(): LiveData<String> = songTitleLiveData
        fun getArtistMetaData(): LiveData<String> = artistMetaData
        fun getAlbumTitleMetaData(): LiveData<String> = albumTitleMetaData
        fun getArtworkMetaData(): LiveData<String> = artworkMetaData
    }

    @OptIn(UnstableApi::class)
    private fun buildMediaSource(): MediaSource {
        val dataSourceFactory = buildHttpDataSource()
        return DefaultMediaSourceFactory(dataSourceFactory)
            .setLiveTargetOffsetMs(1000)
            .createMediaSource(buildMediaItem())
    }

    private fun buildMediaMetaData(): MediaMetadata {
        return MediaMetadata
            .Builder()
            .setAlbumTitle(albumTitleMetaData.value)
            .setTitle(songTitleLiveData.value)
            .setArtist(artistMetaData.value)
            .setAlbumArtist("SLE Album Artist")
            .setIsPlayable(true)
            .setArtworkUri(Constants.uriParser(Constants.STRING_ALBUM))
            .setExtras(null)
            .build()
    }

    @OptIn(UnstableApi::class)
    private fun buildHttpDataSource(): DefaultHttpDataSource.Factory {
        return DefaultHttpDataSource
            .Factory()
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)
            .setUserAgent("SLE Radio")
            .setAllowCrossProtocolRedirects(true)
    }

    @OptIn(UnstableApi::class)
    private fun buildMediaItem(): MediaItem {
        val metaDataSource = buildMediaMetaData()
        return  MediaItem
            .Builder()
            .setLiveConfiguration(
                MediaItem
                    .LiveConfiguration
                    .Builder()
                    .setMaxPlaybackSpeed(1.5f)
                    .build()
            )
            .setMediaMetadata(metaDataSource)
            .setRequestMetadata(
                MediaItem
                    .RequestMetadata
                    .Builder()
                    .setMediaUri(Constants.MP3_URL.toUri())
                    .build()
            )
            .setUri(Constants.uriParser(Constants.MP3_URL))
            .setMimeType(MimeTypes.APPLICATION_ICY)
            .build()
    }


    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }
}
