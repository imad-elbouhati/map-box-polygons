package com.imadev.androidhometestimadelbouhati.db

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.imadev.androidhometestimadelbouhati.db.model.LocationEntity

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(location:LocationEntity)

    @Query("SELECT * FROM LOCATION_TABLE")
    fun getLocationsList():LiveData<List<LocationEntity>>
}