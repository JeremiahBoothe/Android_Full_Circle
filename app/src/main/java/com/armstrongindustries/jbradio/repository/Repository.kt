package com.armstrongindustries.jbradio.repository

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.armstrongindustries.jbradio.data.ApiDao
import com.armstrongindustries.jbradio.data.Constants
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

/**
 * Repository for fetching and managing current song data.
 */
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

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(OkHttpClient.Builder().build())
        .build()

    private val service: ApiDao = retrofit.create(ApiDao::class.java)

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    init {
        startPeriodicFetching()
    }

    private fun startPeriodicFetching() {
        coroutineScope.launch {
            while (isActive) {
                fetchCurrentSong()
                delay(5000) // Fetch every 5 seconds
            }
        }
    }

    suspend fun fetchCurrentSong() {
        try {
            val response = service.getCurrentSong()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    _id.postValue(body.id ?: INT_ERROR_VALUE)
                    _artist.postValue(body.artist.artistName ?: DEFAULT_VALUE)
                    _title.postValue(body.title ?: DEFAULT_VALUE)
                    _album.postValue(body.album ?: DEFAULT_VALUE)
                    _artwork.postValue(body.artwork ?: DEFAULT_ARTWORK)
                }
            } else {
                handleError("HTTP error: ${response.code()}, ${response.errorBody()?.string() ?: "Unknown error"}")
            }
        } catch (e: IOException) {
            handleError("Network error: ${e.message}")
        } catch (e: Exception) {
            handleError("Unexpected error: ${e.message}")
        }
    }

    suspend fun loadImage(url: String): Bitmap? {
        return try {
            val loader = ImageLoader(application)
            val request = ImageRequest.Builder(application)
                .data(url)
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                result.drawable.toBitmap().apply {
                    _imageBitmap.postValue(this)
                    return this
                }
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Repository", "Image loading error: ${e.message}")
            null
        }
    }

    fun uriParser(url: String): Uri = Uri.parse(url)

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getBitmap(context: Context, @DrawableRes drawableId: Int): Bitmap? {
        val drawable: Drawable? = context.getDrawable(drawableId)
        return drawable?.let {
            when (it) {
                is BitmapDrawable -> it.bitmap
                is VectorDrawable -> Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888).apply {
                    val canvas = Canvas(this)
                    it.setBounds(0, 0, canvas.width, canvas.height)
                    it.draw(canvas)
                }
                else -> null
            }
        }
    }

    private fun handleError(message: String) {
        _error.postValue(message)
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
        private const val DEFAULT_ARTWORK = Constants.STRING_ALBUM
    }
}
