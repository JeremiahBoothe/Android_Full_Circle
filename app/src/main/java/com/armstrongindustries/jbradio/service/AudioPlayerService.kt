package com.armstrongindustries.jbradio.service

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.armstrongindustries.jbradio.R
import com.armstrongindustries.jbradio.data.Constants
import com.armstrongindustries.jbradio.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Service class for managing the audio player.
 */
class AudioPlayerService : MediaSessionService(), Player.Listener, MediaController.Listener {

    private lateinit var mediaSession: MediaSession
    private var exoPlayer: ExoPlayer? = null
    private lateinit var mediaNotification: MediaNotification // Declare MediaNotification instance
    private lateinit var repository: Repository // Declare repository instance
    private val serviceScope = CoroutineScope(Dispatchers.Main) // Define a coroutine scope for the service

    private val binder = AudioPlayerServiceBinder()

    override fun onCreate() {
        super.onCreate()
        repository = Repository.getInstance(application) // Initialize the repository
        initializeExoPlayer()
        initializeMediaSession()
        mediaNotification = MediaNotification(this) // Initialize MediaNotification

        // Observe LiveData from the repository to update notification
        repository.title.observeForever { title ->
            updateNotification(title, repository.artist.value, repository.artwork.value)
        }

        repository.artist.observeForever { artist ->
            updateNotification(repository.title.value, artist, repository.artwork.value)
        }

        repository.artwork.observeForever { artwork ->
            updateNotification(repository.title.value, repository.artist.value, artwork)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            AudioPlayerWorker.ACTION_PLAY -> startPlayback()
            // Handle other actions if necessary
        }
        return START_STICKY
    }

    private fun startPlayback() {
        exoPlayer?.apply {
            setHandleAudioBecomingNoisy(true)
            setWakeMode(C.WAKE_MODE_NETWORK)
            setMediaSource(buildMediaSource())
            prepare()
            playWhenReady = true
        }
    }

    private fun updateNotification(songTitle: String?, artist: String?, artworkUrl: String?) {
        val songIcon = R.drawable.sle_radio // Default icon
        mediaNotification.updateNotification(songTitle ?: "Unknown Title", artist ?: "Unknown Artist", songIcon)

        // Load the artwork image if needed within a coroutine
        artworkUrl?.let { url ->
            serviceScope.launch {
                try {
                    repository.loadImage(url) // Load artwork into the repository's LiveData
                } catch (e: Exception) {
                    // Handle the error gracefully (e.g., log it, show a default image, etc.)
                }
            }
        }
    }

    private fun buildMediaSource(): MediaSource {
        val dataSourceFactory = buildHttpDataSource()
        return DefaultMediaSourceFactory(dataSourceFactory)
            .setLiveTargetOffsetMs(1000)
            .createMediaSource(buildMediaItem())
    }

    private fun buildMediaItem(): MediaItem {
        return MediaItem.Builder()
            .setUri(Constants.uriParser(Constants.MP3_URL))
            .setMimeType(MimeTypes.APPLICATION_ICY)
            .build()
    }

    private fun buildHttpDataSource(): DefaultHttpDataSource.Factory {
        return DefaultHttpDataSource.Factory()
            .setConnectTimeoutMs(15000)
            .setReadTimeoutMs(15000)
            .setUserAgent("SLE Radio")
            .setAllowCrossProtocolRedirects(true)
    }

    private fun initializeExoPlayer() {
        exoPlayer = ExoPlayer.Builder(this).build().also {
            it.addListener(this)
        }
    }

    private fun initializeMediaSession() {
        mediaSession = MediaSession.Builder(this, exoPlayer!!).build()
    }

    override fun onDestroy() {
        exoPlayer?.release()
        mediaSession.release()
        super.onDestroy()
    }

    inner class AudioPlayerServiceBinder : Binder() {
        fun getExoPlayerInstance() = exoPlayer
    }

    override fun onBind(intent: Intent?): IBinder {
        return binder
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession {
        return mediaSession
    }

    fun getMediaSession(): MediaSession {
        return mediaSession
    }
}