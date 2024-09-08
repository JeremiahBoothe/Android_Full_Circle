package com.armstrongindustries.jbradio.metadata

import retrofit2.Response
import retrofit2.http.GET

interface APIService {
    @GET("api.laut.fm/station/radiotfsc/current_song/")
    suspend fun getMetaData(): Response<SimpleJSONModel>
}