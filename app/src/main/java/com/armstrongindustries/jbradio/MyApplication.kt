/**
 * Entry point for the application, initializing the database to support app functionality.
 * @author Jeremiah Boothe
 * @date 06/24/2024
 */
package com.armstrongindustries.jbradio

import android.app.Application
import android.util.Log
import androidx.room.Room
import com.armstrongindustries.jbradio.data.ArtistNameData
import com.armstrongindustries.jbradio.data.RadioMetaData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication : Application() {



    override fun onCreate() {
        super.onCreate()

    }

}
