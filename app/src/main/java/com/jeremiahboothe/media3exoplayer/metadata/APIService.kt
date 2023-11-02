package com.jeremiahboothe.media3exoplayer.metadata

import retrofit2.Response
import retrofit2.http.GET

interface APIService {
    @GET("api/stations/umg0tz9sywzuv/now_playing/")
    suspend fun getMetaData(): Response<SimpleJSONModel>
}