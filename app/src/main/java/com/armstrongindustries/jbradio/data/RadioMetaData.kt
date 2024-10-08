package com.armstrongindustries.jbradio.data

import com.google.gson.annotations.SerializedName

data class RadioMetaData(
        @SerializedName("id")
        val id: Int,
        @SerializedName("type")
        val type: String,
        @SerializedName("artist")
        val artistData: ArtistData,
        @SerializedName("title")
        val title: String,
        @SerializedName("album")
        val album: String,
        @SerializedName("artwork")
        val artwork: String,
        @SerializedName("length")
        val length: Int,
        @SerializedName("genre")
        val genre: String,
        @SerializedName("releaseyear")
        val releaseyear: Int,
        @SerializedName("created_at")
        val created_at: String,

        @SerializedName("started_at")
        val started_at: String,
        @SerializedName("ends_at")
        val ends_at: String
)


