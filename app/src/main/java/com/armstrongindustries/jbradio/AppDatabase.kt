package com.armstrongindustries.jbradio

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.armstrongindustries.jbradio.data.RadioMetaData
import com.armstrongindustries.jbradio.data.RadioMetaDataDao
import com.armstrongindustries.jbradio.data.StationDao
import com.armstrongindustries.jbradio.repository.Converters
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


@Database(entities = [RadioMetaData::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class) // Register the converter globally in the database
abstract class AppDatabase : RoomDatabase() {
    abstract fun stationDao(): StationDao
    abstract fun radioMetaDataDao(): RadioMetaDataDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        private const val NUMBER_OF_THREADS = 4
        val databaseWriteExecutor: ExecutorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS)

        /**
         * Gets the AppDatabase instance.
         * @param context The application context.
         * @return The AppDatabase instance.
         */

        @Synchronized
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

