package com.armstrongindustries.jbradio.ui.dashboard

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.armstrongindustries.jbradio.data.RadioMetaData
import com.armstrongindustries.jbradio.repository.RadioRepository
import com.armstrongindustries.jbradio.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val repository2 = Repository.getInstance(application)
    private val repository: RadioRepository = RadioRepository(application)

    val id: LiveData<Int> = repository2.id
    val artist: LiveData<String> = repository2.artist
    val title: LiveData<String> = repository2.title
    val album: LiveData<String> = repository2.album
    val artwork: LiveData<String> = repository2.artwork

    val pagedItems: Flow<PagingData<RadioMetaData>> = repository.getSongItems()
        .cachedIn(viewModelScope)

    companion object {
        private const val FETCH_DELAY_MS = 5000L
    }

    init {
        // Initialize the WorkManager request on ViewModel initialization
        scheduleInventoryWorker()
    }

    @Deprecated("PagedList in favor of PagingData", ReplaceWith("itemList"))
    fun getItemList(): Flow<PagingData<RadioMetaData>> = pagedItems

    private fun scheduleInventoryWorker() {
        val inventoryWorkerRequest: WorkRequest = OneTimeWorkRequest.Builder(
            InventoryNotificationWorker::class.java)
            .setInitialDelay(5, TimeUnit.SECONDS)
            .setInputData(Data.Builder().build())
            .build()

        WorkManager.getInstance(getApplication()).enqueue(inventoryWorkerRequest)
    }

    fun addItem(item: RadioMetaData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.insertItem(item)
            } catch (e: Exception) {
                showToast("Error adding item")
            }
        }
    }

    fun updateItem(item: RadioMetaData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.updateItem(item)
                checkNotificationStatus()
                if (item.id > 0) {
                    removeNotificationForItem(item.id)
                }
            } catch (e: Exception) {
                showToast("Error updating item")
            }
        }
    }

    fun deleteItem(item: RadioMetaData) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.deleteItem(item)
                checkNotificationStatus()
                if (item.id < 1) {
                    removeNotificationForItem(item.id)
                }
            } catch (e: Exception) {
                showToast("Error deleting item")
            }
        }
    }

    private fun removeNotificationForItem(itemId: Int) {
        val updateNotificationRequest: WorkRequest = OneTimeWorkRequest.Builder(
            InventoryNotificationWorker::class.java)
            .setInputData(Data.Builder().putInt("action", 1).putInt("itemId", itemId).build())
            .build()

        WorkManager.getInstance(getApplication()).enqueue(updateNotificationRequest)
    }

    private fun checkNotificationStatus() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val hasItemsWithZeroQuantity = repository.getSongItems()
                withContext(Dispatchers.Main) {
                    if (true) {
                        startNotificationWorker()
                    } else {
                        /*stopNotificationWorker()*/
                    }
                }
            } catch (e: Exception) {
                showToast("Error checking item status")
            }
        }
    }

    private suspend fun showToast(message: String) {
        withContext(Dispatchers.Main) {
            Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
        }
    }

    fun startNotificationWorker() {
        // Implementation for starting notification worker
    }

/*    fun stopNotificationWorker() {
        NotificationScheduler.stopNotificationWorker(getApplication())
    }*/
}
