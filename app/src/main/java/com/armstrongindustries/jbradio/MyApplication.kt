/**
 * Entry point for the application, initializing the database to support app functionality.
 * @author Jeremiah Boothe
 * @date 06/24/2024
 */
package com.armstrongindustries.jbradio

import android.app.Application
import android.util.Log

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initializeDatabase()
    }

    private fun initializeDatabase() {
        try {
            AppDatabase.getDatabase(this)
            Log.d("MyApplication", "Database initialized successfully.")
        } catch (e: Exception) {
            Log.e("MyApplication", "Error initializing database", e)
        }
    }
}
