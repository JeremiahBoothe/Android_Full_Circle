package com.armstrongindustries.jbradio.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object for interacting with the radio_metadata table.
 */
@Dao
interface RadioMetaDataDao {

    /**
     * Retrieves a RadioMetaData entry by its ID.
     *
     * @param id The ID of the RadioMetaData entry to retrieve.
     * @return The corresponding RadioMetaData entry, or null if not found.
     */
    @Query("SELECT * FROM radio_metadata WHERE id = :id")
    suspend fun getRadioMetaDataById(id: Int): RadioMetaData?

    /**
     * Inserts a RadioMetaData entry into the database.
     * If a conflict occurs, the existing entry will be ignored.
     *
     * @param radioMetaData The RadioMetaData entry to insert.
     * @return The row ID of the newly inserted entry, or -1 if the insert failed.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRadioMetaData(radioMetaData: RadioMetaData): Long

    /**
     * Deletes a RadioMetaData entry from the database by its ID.
     *
     * @param id The ID of the RadioMetaData entry to delete.
     * @return The number of rows affected by the delete operation (1 if successful, 0 if not found).
     */
    @Query("DELETE FROM radio_metadata WHERE id = :id")
    suspend fun deleteRadioMetaDataById(id: Int): Int

    /**
     * Retrieves all RadioMetaData entries from the database.
     *
     * @return A list of all RadioMetaData entries.
     */
    @Query("SELECT * FROM radio_metadata")
    suspend fun getAllRadioMetaData(): List<RadioMetaData>
}
