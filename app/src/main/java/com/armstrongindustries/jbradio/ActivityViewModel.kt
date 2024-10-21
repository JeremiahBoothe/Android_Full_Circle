package com.armstrongindustries.jbradio

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.armstrongindustries.jbradio.data.AppDatabase
import com.armstrongindustries.jbradio.data.ArtistNameData
import com.armstrongindustries.jbradio.data.RadioMetaData
import com.armstrongindustries.jbradio.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = Repository.getInstance(application)
    private lateinit var radioDatabase: AppDatabase
    val id: LiveData<Int> = repository.id
    val artist: LiveData<String> = repository.artist
    val title: LiveData<String> = repository.title
    val album: LiveData<String> = repository.album
    val artwork: LiveData<String> = repository.artwork

    init {
        initializeDatabaseObserver()
        initializeDatabase()
    }

    private fun initializeDatabaseObserver() {
        // Observe the LiveData values to perform database initialization when they're available
        id.observeForever { initializeDatabase() }
        artist.observeForever { initializeDatabase() }
        title.observeForever { initializeDatabase() }
        album.observeForever { initializeDatabase() }
        artwork.observeForever { initializeDatabase() }
    }

    private fun initializeDatabase() {
        // Make sure all values are available before proceeding
        if (id.value != null && artist.value != null && title.value != null && album.value != null) {
            try {
                // Initialize the Room Database
                radioDatabase = Room.databaseBuilder(
                    getApplication(),
                    AppDatabase::class.java,
                    "radio_database"
                ).build()

                CoroutineScope(Dispatchers.IO).launch {
                    radioDatabase.radioMetaDataDao().insertRadioMetaData(
                        RadioMetaData(
                            artist = ArtistNameData(artistName = artist.value.toString()),
                            title = title.value.toString(),
                            album = album.value.toString(),
                            id = id.value!!.toInt(),
                            type = "TODO()",
                            artwork = artwork.value.toString(),
                            length = 1,
                            genre = "TODO()",
                            releaseyear = 2024,
                            createdAt = "",
                            startedAt = "",
                            endsAt = ""
                        )
                    )
                }
                Log.d("MyApplication", "Database initialized successfully.")
            } catch (e: Exception) {
                Log.e("MyApplication", "Error initializing database", e)
            }
        } else {
            Log.w("MyApplication", "Database initialization skipped, LiveData values are not yet available.")
        }
    }
}
