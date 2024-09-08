package com.armstrongindustries.jbradio.ui.util

import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.os.Binder
import android.os.IBinder
import androidx.core.net.toUri
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.*
import androidx.media3.common.C.WAKE_MODE_LOCAL
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import androidx.media3.ui.PlayerView
import com.armstrongindustries.jbradio.ui.MainActivity
import com.armstrongindustries.jbradio.R
import com.armstrongindustries.jbradio.metadata.MyVariables


@UnstableApi
class AudioPlayerService : MediaLibraryService(), Player.Listener {
    /*
    //JB Experimenting with passing intent back to player app
    // after swiping notification away
    //private lateinit var intentService: Intent
    */

    private var exoPlayer: ExoPlayer? = null

    private var songTitleLiveData = MutableLiveData<String>()
    private var songDescriptionLiveData = MutableLiveData<String>()
    private var songIconLiveData = MutableLiveData<Int>()

    //JB added to try to display metadata.
    private var artistMetaData = MutableLiveData<String>()
    private var albumTitleMetaData = MutableLiveData<String>()
    private var skuTextViewMetaData = MutableLiveData<String>()
    private var thumbTextViewMetaData = MutableLiveData<String>()

    private var playerNotificationManager: PlayerNotificationManager? = null
    private var binder = AudioPlayerServiceBinder()

    private lateinit var mediaLibrarySession: MediaLibrarySession

    private var playerNotificationListener: PlayerNotificationManager.NotificationListener =
        @UnstableApi object : PlayerNotificationManager.NotificationListener {

            override fun onNotificationCancelled(
                notificationId: Int,
                dismissedByUser: Boolean
            ) {
                //JB added System.exit and onDestroy to terminate app properly not quite working
                //does change the way it terminates in a somewhat favorable way but creates
                //new app and restarts it.  doesn't fully terminate.  Getting close
                stopSelf()
                //val activity: MainActivity = MainActivity()
                //onDestroy()
                //activity.finish()
                //exitProcess(0)
            }

            override fun onNotificationPosted(
                notificationId: Int,
                notification: Notification,
                ongoing: Boolean
            ) {
                if (ongoing) {
                    startForeground(notificationId, notification)
                } else {
                    stopForeground(false)
                }
            }
        }

    /*//JB Collapsed variables
    //private var prettyJsonTextViewMetaData = MutableLiveData<String>()
    //private var audioPlayerServiceBinder: AudioPlayerServiceBinder? = null
    //private lateinit var binding: BasicAudioPlayerWithNotificationBinding
    */
    /*
     *
     * ui part
     * notification cancel when null player pass or dismiss by user
     * after player release, player in manager must be set to null
     * action
     * prev and next
     * prev and next in compact view (lock screen)
     * play and pause
     * stop
     * rewind increment
     * fast forward increment
     * notification icon
     */

    override fun onCreate() {
        super.onCreate()
        startPlayer()
    }

    private fun startPlayer() {

        //JB added mediaSource Builder
        val mediaSource = buildMediaSource()
        //val mediaMetadata = buildMediaMetaData()
        val playerView = PlayerView(this)
        playerView.player = exoPlayer

        exoPlayer = ExoPlayer.Builder(this)
            .build()
        /*  //JB Blocked out original player builder
        val defaultDataSourceFactory = DefaultHttpDataSource.Factory()
        val concatenatingMediaSource = ConcatenatingMediaSource()
        for (sample in Constants.MP3_SAMPLE_PLAYLIST) {
            val progressiveMediaSource =
                ProgressiveMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(sample.uri))
            concatenatingMediaSource.addMediaSource(progressiveMediaSource)
        }*/

        exoPlayer?.let {
            it.addListener(this)
            it.mediaMetadata.artist
            it.setForegroundMode(true)
            it.setMediaSource(mediaSource)
            /*
            //it.getRenderer(3)
            //it.setMediaItem(buildMediaSource().mediaItem)
            //it.setMediaItem(mediaSource.mediaItem)
            //it.mediaMetadata.artist
            //it.playlistMetadata.artist
            //it.getRenderer(3)
            //it.getRendererType(3)
            //it.videoDecoderCounters.toString()
            //concatenatingMediaSource
            //it.mediaMetadata.artworkUri
            //it.mediaMetadata()
            //it.setMediaItem(buildMediaSource().mediaItem)
            */
            it.setWakeMode(WAKE_MODE_LOCAL)
            it.setAudioAttributes(AudioAttributes.Builder()
                .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                .setUsage(C.USAGE_MEDIA)
                .build(),
                true)
            it.prepare()
            it.playWhenReady = true
        }
        setupNotification(this)
    }

    private fun setupNotification(context: AudioPlayerService) {

        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            Constants.PLAYBACK_NOTIFICATION_ID,
            Constants.PLAYBACK_CHANNEL_ID
        )
            .setChannelNameResourceId(R.string.playback_channel_name)
            .setChannelDescriptionResourceId(R.string.playback_channel_description)
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    val intent = Intent(context, MainActivity::class.java)
                    return PendingIntent
                        .getActivity(
                            context,
                            0,
                            intent,
                            PendingIntent.FLAG_MUTABLE
                        )
                }

                override fun getCurrentContentText(player: Player): CharSequence {
                    songDescriptionLiveData.postValue(Constants.MP3_SAMPLE_PLAYLIST[player.currentMediaItemIndex].description)
                    return Constants.MP3_SAMPLE_PLAYLIST[player.currentMediaItemIndex].description
                }

                override fun getCurrentContentTitle(player: Player): CharSequence {
                    songTitleLiveData.postValue(Constants.MP3_SAMPLE_PLAYLIST[player.currentMediaItemIndex].title)
                    return Constants.MP3_SAMPLE_PLAYLIST[player.currentMediaItemIndex].title
                }
                /*
                override fun getCurrentSubText(player: Player): CharSequence {
                    artistMetaData.postValue(sleMetaData.SLEMetaData[player.currentMediaItem].artist)
                    return artist

                }

                //override fun getCurrentSubText(player: Player): CharSequence? {
                //  return super.getCurrentSubText(player)
                // }
                */
                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    songIconLiveData.postValue(Constants.MP3_SAMPLE_PLAYLIST[player.currentMediaItemIndex].bitmapResource)
                    return Constants.getBitmap(
                        context,
                        Constants.MP3_SAMPLE_PLAYLIST[player.currentMediaItemIndex].bitmapResource
                    )
                }
            })
            .setNotificationListener(playerNotificationListener)
            .build()
        playerNotificationManager?.setPlayer(exoPlayer)
    }

    override fun onDestroy() {
        exoPlayer?.let { player ->
            playerNotificationManager?.let {
                it.setPlayer(null)
                playerNotificationManager = null
            }
            player.removeListener(this)
            player.release()
            exoPlayer = null
        }
        //System.exit(0)
        super.onDestroy()
        /*
        //JB Added - Returns Intent to AudioPlayerService upon closing.
        //intentService = Intent(this, BasicAudioPlayerWithNotification::class.java)
        //Util.startForegroundService(this, intentService)
        //exoPlayer?.stop()
        //exoPlayer = null*/
    }

    inner class AudioPlayerServiceBinder : Binder() {
        fun getSimpleExoPlayerInstance() = exoPlayer
        fun getTitleLiveData() = songTitleLiveData
        fun getDescriptionLiveData() = songDescriptionLiveData
        fun getIconLiveData() = songIconLiveData
        //JB Added to try to display Metadata
        fun getArtistMetaData() = artistMetaData
        fun getAlbumTitleMetaData() = albumTitleMetaData
        fun getSkuTextViewMetaData() = skuTextViewMetaData
        fun getThumbTextViewMetaData() = thumbTextViewMetaData
        //fun getPrettyJsonTextViewMetaData() = prettyJsonTextViewMetaData
    }

    override fun onBind(intent: Intent?): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //replaced startID with the Int 1 - This did not work, still opens with a new startID
        return super.onStartCommand(intent, flags, startId)
    }
    // creating a HLSMediaSource(ProgressiveMediaSource)
    private fun buildMediaSource(): MediaSource {
        // using DefaultHttpDataSourceFactory for http data source
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setAllowCrossProtocolRedirects(true)
        /*// Set a custom authentication request header
        // dataSourceFactory.setDefaultRequestProperty("Header", "Value")
        // setAllowChunklessPreparation()
        // create HLS Media Source */
        val metaDataSource = MediaMetadata.Builder()
            .setAlbumTitle("SLE Album Title")
            .setTitle("SLE title ")
            .setArtist("Sle Artist")
            .setAlbumArtist("SLE Album Artist")
            .setIsPlayable(true)
            .setArtworkUri(Constants.uriParser(MyVariables.stringAlbum))
            .build()

        return DefaultMediaSourceFactory(dataSourceFactory)
            .createMediaSource(
                MediaItem
                    .Builder()
                    .setMediaMetadata(metaDataSource)
                    .setRequestMetadata(
                        MediaItem
                            .RequestMetadata
                            .Builder()
                            .setMediaUri(
                                Constants.MP3_URL.toUri())
                            .build())
                    .setUri(Constants.uriParser(Constants.MP3_URL))
                    //JB attempt at setLiveConfiguration, does it even work?
                    // don't know but it's not broken
                    .setLiveConfiguration(MediaItem.LiveConfiguration.Builder()
                        .setMinPlaybackSpeed(0.97f)
                        .setMaxPlaybackSpeed(1.0f)
                        .setTargetOffsetMs(5000)
                        .build()
                    )
                    //.setUri(getString(R.string.media_url_id3))
                    .setMimeType(MimeTypes.APPLICATION_ICY)
                    //.setUri(Constants.uriParser(url))
                    .build()
            )
    }
}