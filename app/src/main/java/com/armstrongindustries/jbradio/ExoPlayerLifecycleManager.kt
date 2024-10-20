package com.armstrongindustries.jbradio

import androidx.appcompat.app.AppCompatActivity
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerControlView

class ExoPlayerLifecycleManager(
    private val activity: AppCompatActivity,
    private val playerControlView: PlayerControlView
) {
    private var exoPlayer: ExoPlayer? = null

    fun start() {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(activity).build().apply {
                // Prepare the player for playback
                prepare()
                playWhenReady = true
                playerControlView.player = this
            }
        }
    }

    fun stop() {
        exoPlayer?.release()
        exoPlayer = null
    }

    fun updateId(id: Int) {
        // Logic to update player with new ID
        // This could involve updating the media source based on the ID
    }

    fun updateArtist(artist: String) {
        // Logic to update player with new artist
    }

    fun updateTitle(title: String) {
        // Logic to update player with new title
    }

    fun updateAlbum(album: String) {
        // Logic to update player with new album
    }

    fun updateArtwork(artwork: String) {
        // Logic to update player with new artwork
    }
}
