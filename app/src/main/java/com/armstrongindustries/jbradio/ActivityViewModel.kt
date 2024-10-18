package com.armstrongindustries.jbradio

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.armstrongindustries.jbradio.data.ArtistNameData
import com.armstrongindustries.jbradio.data.RadioMetaData
import com.armstrongindustries.jbradio.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class ActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = Repository.getInstance(application)
    private lateinit var radioDatabase: AppDatabase
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
        initializeDatabase()
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


    private fun initializeDatabase() {
        try {
            // Initialize the Room Database
            radioDatabase = Room.databaseBuilder(
                getApplication(),
                AppDatabase::class.java,
                "radio_database"
            ).build()

            // Populate the database with dummy data
            CoroutineScope(Dispatchers.IO)
                .launch(Dispatchers.IO) {
                    radioDatabase.radioMetaDataDao().insertRadioMetaData(
                        RadioMetaData(
                            artist = ArtistNameData(artistName = artist.value.toString()),
                            title = title.value.toString(),
                            album = album.value.toString(),
                            id = id.value!!.toInt(),
                            type = "TODO()",
                            artwork = "TODO()",
                            length = 1,
                            genre = "TODO()",
                            releaseyear = 2024,
                            createdAt = "",
                            startedAt = "",
                            endsAt = ""
                        )
                    )
                }
            AppDatabase.getDatabase(getApplication())
            Log.d("MyApplication", "Database initialized successfully.")
        } catch (e: Exception) {
            Log.e("MyApplication", "Error initializing database", e)
        }
    }

}
