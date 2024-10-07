package com.armstrongindustries.jbradio.ui.service

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import androidx.annotation.DrawableRes

object Constants {

    // Media URLs
    const val MP3_URL = "https://radiotsc.stream.laut.fm/radiotfsc"
    // ... other media URLs ...

    // Sample playlist
//    val MP3_SAMPLE_PLAYLIST = arrayOf( //       Media(
//            uriParser("https://radiotsc.stream.laut.fm/radiotfsc"),
//            "audio_1",
//            "SLE Radio",
//            "Indie Music for the Masses",
//
//            R.drawable.sle_radio
//        ),
//        Media(
//            uriParser("https://storage.googleapis.com/automotive-media/The_Messenger.mp3"),
//            "audio_2",
//           "The messenger",
//            "Hipster guide to London",
//            R.drawable.goku_second
//        ),
//        Media(
//            uriParser("https://storage.googleapis.com/automotive-media/Talkies.mp3"),
//            "audio_3",
//            "Talkies",
//            "If it talks like a duck and walks like a duck.",
//            R.drawable.goku_third
//        )
//    )

    // Playback notification constants
    const val PLAYBACK_CHANNEL_ID = "playback_channel"
    const val PLAYBACK_NOTIFICATION_ID: Int = 1

    /**
     * Converts a URL string to a Uri object.
     */
    fun uriParser(url: String): Uri {
        return Uri.parse(url)
    }

    /**
     * Converts a drawable resource to a Bitmap object.
     * @param context the context used to access resources.
     * @param drawableId the drawable resource ID.
     * @return the Bitmap representation of the drawable, or null if it cannot be converted.
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun getBitmap(context: Context, @DrawableRes drawableId: Int): Bitmap? {
        val drawable: Drawable? = context.getDrawable(drawableId)
        return drawable?.let {
            when (it) {
                is BitmapDrawable -> it.bitmap
                is VectorDrawable -> {
                    val bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    it.setBounds(0, 0, canvas.width, canvas.height)
                    it.draw(canvas)
                    bitmap
                }
                else -> null
            }
        }
    }
}
