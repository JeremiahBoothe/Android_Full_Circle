package com.armstrongindustries.jbradio.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val id: Int,
    val artist: String,
    val title: String,
    val album: String,
    val artworkUrl: String
) : Parcelable {}
