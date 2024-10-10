package com.armstrongindustries.jbradio

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.armstrongindustries.jbradio.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)

    val id: LiveData<Int> = repository.id
    val artist: LiveData<String> = repository.artist
    val title: LiveData<String> = repository.title
    val album: LiveData<String> = repository.album
    val artwork: LiveData<String> = repository.artwork

    companion object {
        private const val FETCH_DELAY_MS = 5000L
        private const val MAX_DELAY_MS = 60000L // 1 minute
    }

    init {
        startFetchingData()
    }

    /**
     * Starts a coroutine to periodically fetch data from the repository.
     */
    private fun startFetchingData() {
        viewModelScope.launch(Dispatchers.IO) {
            var currentDelay = FETCH_DELAY_MS
            while (isActive) {
                try {
                    repository.fetchCurrentSong()
                    currentDelay = FETCH_DELAY_MS // Reset delay after successful fetch
                } catch (e: Exception) {
                    e.printStackTrace()
                    currentDelay = (currentDelay * 2).coerceAtMost(MAX_DELAY_MS)
                }
                delay(currentDelay)
            }
        }
    }
}
