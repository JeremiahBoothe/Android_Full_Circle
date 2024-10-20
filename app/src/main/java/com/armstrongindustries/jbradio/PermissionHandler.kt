package com.armstrongindustries.jbradio

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class PermissionHandler(
    private val activity: AppCompatActivity,
    private val onPermissionGranted: () -> Unit
) {
    private val requestNotificationPermissionLauncher: ActivityResultLauncher<String> =
        activity.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                onPermissionGranted()
            } else {
                onPermissionResult(false) // Invoke the result callback with false
            }
        }

    var onPermissionResult: (Boolean) -> Unit = { } // Default no-op callback

    fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onPermissionGranted()
        }
    }
}
