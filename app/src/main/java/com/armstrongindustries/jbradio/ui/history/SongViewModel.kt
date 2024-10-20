package com.armstrongindustries.jbradio.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.paging.PagingData
import androidx.paging.map
import com.armstrongindustries.jbradio.data.Song
import com.armstrongindustries.jbradio.repository.RadioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SongViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RadioRepository(application)

    fun fetchSongs(): Flow<PagingData<Song>> {
        return repository.getSongItems().map { pagingData ->
            pagingData.map { metaData ->
                Song(
                    id = metaData.id,
                    artist = (metaData.artist.artistName ?: "Unknown Artist").toString(),
                    title = metaData.title ?: "Untitled",
                    album = metaData.album ?: "Unknown Album",
                    artworkUrl = metaData.artwork ?: ""
                )
            }
        }
    }
}
