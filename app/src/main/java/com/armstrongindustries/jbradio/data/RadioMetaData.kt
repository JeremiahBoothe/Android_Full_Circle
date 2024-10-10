package com.armstrongindustries.jbradio.data

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Parcelable
import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.IOException
import java.net.URL

/**
 * Data class representing metadata for a radio station.
 */
@Parcelize
@Entity(tableName = "radio_metadata")
data class RadioMetaData(
        @PrimaryKey(autoGenerate = false)
        @SerializedName("id")
        val id: Int,

        @SerializedName("type")
        val type: String,

        @SerializedName("artist")
        val artist: ArtistData,

        @SerializedName("title")
        val title: String,

        @SerializedName("album")
        val album: String,

        @SerializedName("artwork")
        val artwork: String?, // Make artwork nullable

        @SerializedName("length")
        val length: Int,

        @SerializedName("genre")
        val genre: String,

        @SerializedName("releaseyear")
        val releaseyear: Int,

        @SerializedName("created_at")
        val createdAt: String,

        @SerializedName("started_at")
        val startedAt: String,

        @SerializedName("ends_at")
        val endsAt: String
) : Parcelable {

        /**
         * Loads the artwork image as a Drawable from a URL or a file path.
         * This method should ideally be called asynchronously.
         *
         * @param context The context used to access resources.
         * @return A Drawable representation of the artwork, or null if the artwork is not available.
         */
        fun getImageDrawable(context: Context): Drawable? {
                return artwork?.let { artPath ->
                        try {
                                // Load the image from a URL
                                val bitmap = BitmapFactory.decodeStream(URL(artPath).openStream())
                                BitmapDrawable(context.resources, bitmap)
                        } catch (e: IOException) {
                                // Log the exception for debugging purposes
                                Log.e("RadioMetaData", "Error loading artwork: ${e.message}", e)
                                null
                        }
                }
        }

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as RadioMetaData

                return id == other.id &&
                        type == other.type &&
                        artist == other.artist &&
                        title == other.title &&
                        album == other.album &&
                        artwork == other.artwork &&
                        length == other.length &&
                        genre == other.genre &&
                        releaseyear == other.releaseyear
        }

        override fun hashCode(): Int {
                return id.hashCode()
        }
}
