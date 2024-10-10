package com.armstrongindustries.jbradio.repository

import androidx.room.TypeConverter
import com.armstrongindustries.jbradio.data.ArtistData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    /**
     * Converts an ArtistData object to a JSON string.
     *
     * @param artistData The ArtistData object to be converted.
     * @return A JSON string representation of the ArtistData object.
     */
    @TypeConverter
    fun fromArtistData(artistData: ArtistData?): String? {
        return artistData?.let { gson.toJson(it) }
    }

    /**
     * Converts a JSON string to an ArtistData object.
     *
     * @param data The JSON string to be converted.
     * @return The ArtistData object parsed from the JSON string.
     */
    @TypeConverter
    fun toArtistData(data: String?): ArtistData? {
        return data?.let {
            val type = object : TypeToken<ArtistData>() {}.type
            gson.fromJson(it, type)
        }
    }
}
