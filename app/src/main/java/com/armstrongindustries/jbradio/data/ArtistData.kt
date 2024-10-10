package com.armstrongindustries.jbradio.data

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArtistData(
    @SerializedName("name")
    val name: String
) : Parcelable
