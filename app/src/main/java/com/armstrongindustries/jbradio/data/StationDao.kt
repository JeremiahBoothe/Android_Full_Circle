package com.armstrongindustries.jbradio.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

/**
 * Data Access Object for interacting with the radio_metadata table.
 */
@Dao
interface StationDao {

    /**
     * Provides a PagingSource for loading RadioMetaData items in a paginated manner.
     *
     * @return A PagingSource that loads RadioMetaData items ordered by title.
     */
    @Query("SELECT * FROM radio_metadata ORDER BY title ASC")
    fun getPagingSource(): PagingSource<Int, RadioMetaData>

    /**
     * Inserts a new RadioMetaData item into the database.
     * If a conflict occurs, the existing item will be replaced.
     *
     * @param item The RadioMetaData item to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRadioMetaData(item: RadioMetaData)

    /**
     * Deletes a specific RadioMetaData item from the database.
     *
     * @param item The RadioMetaData item to delete.
     */
    @Delete
    suspend fun deleteRadioMetaData(item: RadioMetaData)

    /**
     * Updates an existing RadioMetaData item in the database.
     * If a conflict occurs, the existing item will be replaced.
     *
     * @param item The RadioMetaData item to update.
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRadioMetaData(item: RadioMetaData)

    /**
     * Retrieves a RadioMetaData item by its ID.
     *
     * @param id The ID of the RadioMetaData item to retrieve.
     * @return The RadioMetaData item with the specified ID, or null if not found.
     */
    @Query("SELECT * FROM radio_metadata WHERE id = :id")
    suspend fun getRadioMetaDataById(id: Int): RadioMetaData?
}
