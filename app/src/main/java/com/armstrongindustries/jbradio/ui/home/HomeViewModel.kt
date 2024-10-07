package com.armstrongindustries.jbradio.ui.home

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armstrongindustries.jbradio.data.Repository
import com.armstrongindustries.jbradio.ui.metadata.APIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author Jeremiah Boothe
 * @version 1.0
 * @since 2023-09-21
 * @see ViewModel
 * @see MutableLiveData
 * @see LiveData
 * @see Retrofit
 * @see GsonConverterFactory
 * @see APIService
 * @see HomeViewModel
 * @see viewModelScope
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val repository = Repository.getInstance(application)

    val id: LiveData<Int> = repository.id
    val artist: LiveData<String> = repository.artist
    val title: LiveData<String> = repository.title
    val album: LiveData<String> = repository.album
    val artwork: LiveData<String> = repository.artwork
    val imageBitmap: LiveData<Bitmap?> = repository.imageBitmap

    companion object {
        private const val FETCH_DELAY_MS = 5000L
    }
}
