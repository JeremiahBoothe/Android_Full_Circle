package com.armstrongindustries.jbradio.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.armstrongindustries.jbradio.repository.Repository

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository.getInstance(application)

    val id: LiveData<Int> = repository.id
    val artist: LiveData<String> = repository.artist
    val title: LiveData<String> = repository.title
    val album: LiveData<String> = repository.album
    val artwork: LiveData<String> = repository.artwork

    companion object {
        private const val FETCH_DELAY_MS = 5000L
    }

}
