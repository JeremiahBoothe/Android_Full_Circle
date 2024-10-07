// File: com/example/myapplication/data/Repository.kt

package com.armstrongindustries.jbradio.data

import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.armstrongindustries.jbradio.ui.metadata.APIService
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class Repository private constructor(private val application: Application) {

    private val _id = MutableLiveData<Int>()
    val id: LiveData<Int> get() = _id

    private val _artist = MutableLiveData<String>()
    val artist: LiveData<String> get() = _artist

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    private val _album = MutableLiveData<String>()
    val album: LiveData<String> get() = _album

    private val _artwork = MutableLiveData<String>()
    val artwork: LiveData<String> get() = _artwork



    private val _imageBitmap = MutableLiveData<Bitmap?>()
    val imageBitmap: LiveData<Bitmap?> get() = _imageBitmap

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.laut.fm/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    private val service: APIService = retrofit.create(APIService::class.java)

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getInstance(application: Application): Repository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Repository(application).also { INSTANCE = it }
            }
        }

        private const val DEFAULT_VALUE = "N/A"
        private const val ERROR_VALUE = "Error"
        private const val INT_ERROR_VALUE = 0
        private const val DEFAULT_ARTWORK = "https://assets.laut.fm/5140d46b992d1e3772baf4b4000b276f"
    }

    init {
        startPeriodicFetching()
    }

    private fun startPeriodicFetching() {
        coroutineScope.launch {
            while (isActive) {
                fetchCurrentSong()
                delay(5000) // Fetch data every 5 seconds
            }
        }
    }

    suspend fun fetchCurrentSong() {
        try {
            val response = service.getCurrentSong()
            if (response.isSuccessful) {
                val body = response.body()
                _id.postValue(body?.id ?: INT_ERROR_VALUE)
                _artist.postValue(body?.artist?.name ?: DEFAULT_VALUE)
                _title.postValue(body?.title ?: DEFAULT_VALUE)
                _album.postValue(body?.album ?: DEFAULT_VALUE)
                _artwork.postValue(body?.artwork ?: DEFAULT_ARTWORK)
            } else {
                handleError("HTTP error: ${response.code()}")
            }
        } catch (e: IOException) {
            handleError("Network error: ${e.message}")
        } catch (e: Exception) {
            handleError("Unexpected error: ${e.message}")
        }
    }

    suspend fun loadImage(url: String): Drawable? {
        try {
            val imageLoader = ImageLoader(application)
            val request = ImageRequest.Builder(application)
                .data(url)
                .build()

            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                val bitmap: Drawable = result.drawable
                _imageBitmap.postValue(bitmap.toBitmap())
            }
        } catch (e: Exception) {
            Log.e("Repository", "Image loading error: ${e.message}")
        }
        return _imageBitmap as Drawable?
    }

    private fun handleError(message: String) {
        _id.postValue(INT_ERROR_VALUE)
        _artist.postValue(ERROR_VALUE)
        _title.postValue(ERROR_VALUE)
        _album.postValue(ERROR_VALUE)
        _artwork.postValue(ERROR_VALUE)
        Log.e("Repository", message)
    }

    fun clear() {
        coroutineScope.cancel()
    }
}
