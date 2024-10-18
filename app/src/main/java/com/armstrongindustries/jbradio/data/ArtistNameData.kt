package com.armstrongindustries.jbradio.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * Data class representing the radio metadata.
 * @param artistName The name of the current artist.
 */
@Parcelize
data class ArtistNameData(
    @SerializedName("name")
    val artistName: String
) : Parcelable
