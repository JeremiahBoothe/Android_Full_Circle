package com.armstrongindustries.jbradio.ui.notifications

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.armstrongindustries.jbradio.data.Song
import kotlinx.coroutines.launch

class SongViewModel(application: Application) : AndroidViewModel(application) {
    private val _songs = MutableLiveData<List<Song>>()
    val songs: LiveData<List<Song>> get() = _songs

    init {
        // Fetch or load the list of songs here
        fetchSongs()
    }

    private fun fetchSongs() {
        viewModelScope.launch {
            // Simulate fetching songs from a repository or API
            val songList = listOf(
                Song(1, "Artist 1", "Title 1", "Album 1", "https://example.com/artwork1.jpg"),
                Song(2, "Artist 2", "Title 2", "Album 2", "https://example.com/artwork2.jpg")
                // Add more songs as needed
            )
            _songs.postValue(songList)
        }
    }
}


