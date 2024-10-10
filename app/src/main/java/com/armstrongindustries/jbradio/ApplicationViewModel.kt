package com.armstrongindustries.jbradio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.armstrongindustries.jbradio.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ApplicationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: Repository = Repository.getInstance(application)
    val id: LiveData<Int> = repository.id
    val artist: LiveData<String> = repository.artist
    val title: LiveData<String> = repository.title
    val album: LiveData<String> = repository.album
    val artwork: LiveData<String> = repository.artwork

    companion object {
        private const val FETCH_DELAY_MS = 5000L
    }

    init {
        startFetchingData()
    }

    /**
     * Starts a coroutine to periodically fetch data from the repository.
     */
    private fun startFetchingData() {
        viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                try {
                    repository.fetchCurrentSong()
                } catch (e: Exception) {
                    // Log the error or handle it in a way that doesn't crash the app
                    e.printStackTrace()
                }
                delay(FETCH_DELAY_MS) // Fetch data every 5 seconds
            }
        }
    }
}