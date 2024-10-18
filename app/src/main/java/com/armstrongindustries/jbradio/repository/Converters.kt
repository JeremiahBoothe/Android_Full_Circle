package com.armstrongindustries.jbradio.repository

import androidx.room.TypeConverter
import com.armstrongindustries.jbradio.data.ArtistNameData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type converters for Room database.
 * @property gson Gson instance for JSON conversion.
 * @property fromArtistData Converts an ArtistData object to a JSON string.
 * @property toArtistData Converts a JSON string to an ArtistData object.
 * @return A TypeConverter for Room database.
 */
class Converters {

    private val gson = Gson()

    /**
     * Converts an ArtistData object to a JSON string.
     *
     * @param artistNameData The ArtistData object to be converted.
     * @return A JSON string representation of the ArtistData object.
     */
    @TypeConverter
    fun fromArtistData(artistNameData: ArtistNameData?): String? {
        return artistNameData?.let { gson.toJson(it) }
    }

    /**
     * Converts a JSON string to an ArtistData object.
     *
     * @param data The JSON string to be converted.
     * @return The ArtistData object parsed from the JSON string.
     */
    @TypeConverter
    fun toArtistData(data: String?): ArtistNameData? {
        return data?.let {
            val type = object : TypeToken<ArtistNameData>() {}.type
            gson.fromJson(it, type)
        }
    }
}
