package com.armstrongindustries.jbradio.repository

import android.content.Context
import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.armstrongindustries.jbradio.data.AppDatabase
import com.armstrongindustries.jbradio.data.RadioMetaData
import com.armstrongindustries.jbradio.data.StationDao
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository class for managing RadioMetaData items.
 * @param context The application context.
 */
class RadioRepository(private val context: Context) {

    private val stationDao: StationDao = AppDatabase.getDatabase(context).stationDao()
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    /**
     * Registers a new song by inserting its metadata into the database.
     * The operation runs asynchronously.
     *
     * @param song The RadioMetaData object representing the song to register.
     */
    private fun register(song: RadioMetaData) {
        repositoryScope.launch(Dispatchers.IO) {
            runCatching {
                stationDao.insertRadioMetaData(song)
            }.onFailure { e ->
                Log.e("RadioRepository", "Error registering song: ${e.message}", e)
            }
        }
    }

    /**
     * Deletes a specific RadioMetaData item from the database.
     *
     * @param item The RadioMetaData item to delete.
     */
    suspend fun deleteItem(item: RadioMetaData) {
        withContext(Dispatchers.IO) {
            runCatching {
                stationDao.deleteRadioMetaData(item)
            }.onFailure { e ->
                Log.e("RadioRepository", "Error deleting item: ${e.message}", e)
            }
        }
    }

    /**
     * Inserts a new RadioMetaData item into the database.
     *
     * @param item The RadioMetaData item to insert.
     */
    suspend fun insertItem(item: RadioMetaData) {
        withContext(Dispatchers.IO) {
            runCatching {
                stationDao.insertRadioMetaData(item)
            }.onFailure { e ->
                Log.e("RadioRepository", "Error inserting item: ${e.message}", e)
            }
        }
    }

    /**
     * Updates an existing RadioMetaData item in the database.
     *
     * @param item The RadioMetaData item to update.
     */
    suspend fun updateItem(item: RadioMetaData) {
        withContext(Dispatchers.IO) {
            runCatching {
                stationDao.updateRadioMetaData(item)
            }.onFailure { e ->
                Log.e("RadioRepository", "Error updating item: ${e.message}", e)
            }
        }
    }

    /**
     * Retrieves a Flow of PagingData for RadioMetaData items.
     *
     * @return A Flow of PagingData containing RadioMetaData items.
     */
    fun getSongItems(): Flow<PagingData<RadioMetaData>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { stationDao.getPagingSource() }
        ).flow
    }

    /**
     * Cancel any ongoing coroutines when the repository is no longer needed.
     */
    fun onCleared() {
        repositoryScope.cancel()
    }
}
