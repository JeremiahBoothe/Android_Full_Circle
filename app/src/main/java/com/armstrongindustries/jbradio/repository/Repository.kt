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
 * Repository class for managing data related to the current song.
 * @param application The application context.
 * @property _id The MutableLiveData for the song ID.
 * @property id The LiveData for the song ID.
 * @property _artist The MutableLiveData for the artist name.
 * @property artist The LiveData for the artist name.
 * @property _title The MutableLiveData for the song title.
 * @property title The LiveData for the song title.
 * @property _album The MutableLiveData for the album name.
 * @property album The LiveData for the album name.
 * @property _artwork The MutableLiveData for the artwork URL.
 * @property artwork The LiveData for the artwork URL.
 * @property _imageBitmap The MutableLiveData for the artwork Bitmap.
 * @property imageBitmap The LiveData for the artwork Bitmap.
 * @property _error The MutableLiveData for error messages.
 * @property error The LiveData for error messages.
 * @property retrofit The Retrofit instance for making API requests.
 * @property service The ApiDao instance for making API requests.
 * @property coroutineScope The coroutine scope for performing repository operations.
 * @property startPeriodicFetching Starts periodic fetching of current song data.
 * @property fetchCurrentSong Fetches the current song data from the API.
 * @property loadImage Loads the artwork image from the given URL.
 * @property uriParser Parses the given URL into a URI.
 * @property getBitmap Retrieves a Bitmap from a drawable resource.
 * @property handleError Handles errors by updating LiveData and logging them.
 * @property clear Cancels the coroutine scope for repository operations.
 * @return A Repository instance.
 * @see Repository
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

    /**
     * Companion object for the Repository class.
     * @property INSTANCE The singleton instance of the Repository.
     * @property getInstance Returns the singleton instance of the Repository.
     * @property startPeriodicFetching Starts periodic fetching of current song data.
     * @property fetchCurrentSong Fetches the current song data from the API.
     * @property loadImage Loads the artwork image from the given URL.
     * @property uriParser Parses the given URL into a URI.
     * @property getBitmap Retrieves a Bitmap from a drawable resource.
     * @property handleError Handles errors by updating LiveData and logging them.
     * @property clear Cancels the coroutine scope for repository operations.
     * @return A Repository instance.
     * @see Repository
     */
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
                _artist.postValue(body?.artist?.artistName ?: DEFAULT_VALUE)
                _title.postValue(body?.title ?: DEFAULT_VALUE)
                _album.postValue(body?.album ?: DEFAULT_VALUE)
                _artwork.postValue(body?.artwork ?: DEFAULT_ARTWORK)
            } else {
                val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                handleError("HTTP error: ${response.code()}, $errorMessage")
            }
        } catch (e: IOException) {
            handleError("Network error: ${e.message}")
        } catch (e: Exception) {
            handleError("Unexpected error: ${e.message}")
        }
    }

    suspend fun loadImage(url: String): Bitmap? {
        return try {
            val imageLoader = ImageLoader(application)
            val request = ImageRequest.Builder(application)
                .data(url)
                .build()

            val result = imageLoader.execute(request)
            if (result is SuccessResult) {
                val bitmap: Drawable = result.drawable
                _imageBitmap.postValue(bitmap.toBitmap())
                bitmap.toBitmap()
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("Repository", "Image loading error: ${e.message}")
            null
        }
    }

    fun uriParser(url: String): Uri {
        return Uri.parse(url)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun getBitmap(context: Context, @DrawableRes drawableId: Int): Bitmap? {
        val drawable: Drawable? = context.getDrawable(drawableId)
        return drawable?.let {
            when (it) {
                is BitmapDrawable -> it.bitmap
                is VectorDrawable -> {
                    val bitmap = Bitmap.createBitmap(it.intrinsicWidth, it.intrinsicHeight, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(bitmap)
                    it.setBounds(0, 0, canvas.width, canvas.height)
                    it.draw(canvas)
                    bitmap
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
}
