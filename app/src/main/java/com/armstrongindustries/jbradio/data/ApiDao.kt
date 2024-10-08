package com.armstrongindustries.jbradio.data

import retrofit2.Response
import retrofit2.http.GET

interface ApiDao {
    @GET("station/radiotfsc/current_song")
    suspend fun getCurrentSong(): Response<RadioMetaData>
}
