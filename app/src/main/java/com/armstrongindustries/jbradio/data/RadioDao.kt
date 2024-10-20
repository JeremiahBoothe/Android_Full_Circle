package com.armstrongindustries.jbradio.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import com.armstrongindustries.jbradio.data.RadioMetaData

@Dao
interface RadioDao {

    @Query("SELECT * FROM songs ORDER BY id ASC") // Adjust your query as needed
    fun getAllSongs(): PagingSource<Int, RadioMetaData>
}
