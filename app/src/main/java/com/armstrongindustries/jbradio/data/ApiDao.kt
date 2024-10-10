package com.armstrongindustries.jbradio.data

import retrofit2.Response
import retrofit2.http.GET

/**
 * Interface for defining API calls related to radio metadata.
 */
interface ApiDao {

    /**
     * Retrieves the current song information from the radio station.
     *
     * @return A [Response] wrapper containing the [RadioMetaData] object.
     */
    @GET("station/radiotfsc/current_song")
    suspend fun getCurrentSong(): Response<RadioMetaData>

    /**
     * Sealed class to represent the result of the API call.
     */
    sealed class Result {
        data class Success(val data: RadioMetaData) : Result()
        data class Error(val message: String) : Result()
    }
}
