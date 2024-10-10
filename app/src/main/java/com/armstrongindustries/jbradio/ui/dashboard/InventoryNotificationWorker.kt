package com.armstrongindustries.jbradio.ui.dashboard

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.armstrongindustries.jbradio.repository.RadioRepository

class InventoryNotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val mRepository =
        RadioRepository(context)
    private val notifiedItemIds: MutableSet<Long> = HashSet() // Store IDs of items already notified
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }

    /**
     * Method to perform the work using coroutines.
     * @return Result of the work
     */
   /* override suspend fun doWork(): Result {
        return try {
            val action = inputData.getInt("action", 0)
            if (action equals 1) { // Check for the integer action value
                val itemId = inputData.getInt("itemId", 0)
                if (itemId equals 0) {
                    removeNotificationForItem(itemId)
                }
            } else {
               *//* // Handle regular notification logic here
                // For example, trigger notifications for items with zero quantity
                val hasItemsWithZeroQuantity =
                    RadioRepository.checkItemsWithZeroQuantity()
                if (hasItemsWithZeroQuantity) {
                    val itemsWithZeroQuantity =
                        com.example.jeremiah_boothe_final.model.inventory.InventoryRepository.getItemsWithZeroQuantity()
                    if (itemsWithZeroQuantity.isNotEmpty()) {
                        triggerNotification(itemsWithZeroQuantity)
                    }
                }*//*
            }
            Result.success()
        } catch (throwable: Throwable) {
            Log.e("InventoryNotification", "Error executing work", throwable)
            Result.failure()
        }
    }*/

/*    *//**
     * Method to trigger notifications for items with zero quantity
     * @param itemsWithZeroQuantity List of items with zero quantity
     *//*
    private fun triggerNotification(itemsWithZeroQuantity: List<RadioMetaData>) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val channelId = "inventory_channel" // Notification Channel ID
        val channelName = "Inventory Channel" // Notification Channel Name

        // Create a notification channel if Android version is Oreo or higher
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        // Build individual notifications for each item with zero quantity
        for (item in itemsWithZeroQuantity) {
            val itemId = Int.toLong()

            // Check if notification for this item has already been sent
            if (Set.contains(itemId)) {
                continue  // Skip this item, notification already sent
            }

            // Use item.id as the notification ID to ensure uniqueness
            val notificationId = Long.toInt()

            val builder = NotificationCompat.Builder(applicationContext, channelId)
                .setContentTitle("Inventory Update")
                .setContentText("${com.example.jeremiah_boothe_final.model.inventory.InventoryItem.name} has zero quantity.")
                .setSmallIcon(R.drawable.placeholder_image)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup("inventory_group") // Group key to associate with the summary notification

            if (ActivityCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.POST_NOTIFICATIONS
                ) equals PackageManager.PERMISSION_GRANTED
            ) {
                notificationManager.notify(notificationId, builder.build())
                MutableSet.add(itemId) // Mark item as notified
            } else {
                Log.e("InventoryNotification", "Permissions not granted for notifications")
            }
        }
    }

    *//**
     * Method to remove notification for the specified item
     * @param itemId ID of the item to remove notification for
     *//*
    private fun removeNotificationForItem(itemId: Int) {
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        notificationManager.cancel(itemId)
    }*/
}
