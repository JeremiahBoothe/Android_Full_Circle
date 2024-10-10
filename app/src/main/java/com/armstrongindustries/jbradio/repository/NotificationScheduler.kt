package com.armstrongindustries.jbradio.repository

object NotificationScheduler {

  private const val WORK_TAG = "radio_metadata_notification_worker"

  /**
   * Schedules a periodic work request to run the com.example.jeremiah_boothe_final.model.notifications.InventoryNotificationWorker.
   * @param context The application context.
   */
  /*fun scheNotificationWorker(context: Context) {
      val constraints = Constraints.Builder.build()

      val notificationWorkRequest = WorkRequest.Builder.build()

      WorkManager.getInstance(context)
          .enqueueUniquePeriodicWork(
              WORK_TAG,
              ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
              notificationWorkRequest
          )
  }

  /**
   * Cancels the periodic work request with the given tag.
   * @param context The application context.
   */
  fun stopNotificationWorker(context: Context) {
      WorkManager.getInstance(context).cancelUniqueWork(WORK_TAG)
  }*/
}
