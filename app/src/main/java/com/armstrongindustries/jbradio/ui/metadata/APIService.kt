package com.armstrongindustries.jbradio.ui.metadata

import com.armstrongindustries.jbradio.data.RadioMetaData
import retrofit2.Response
import retrofit2.http.GET

interface APIService {
    @GET("station/radiotfsc/current_song")
    suspend fun getCurrentSong(): Response<RadioMetaData>
}
