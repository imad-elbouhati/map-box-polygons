package com.imadev.androidhometestimadelbouhati.repository

import com.imadev.androidhometestimadelbouhati.db.LocationDao
import com.imadev.androidhometestimadelbouhati.db.model.LocationEntity

class MapRepository(private val dao:LocationDao) {
    val locationCoordinatesList = dao.getLocationsList()

    suspend fun insert(location:LocationEntity){
        dao.insertLocation(location)
    }
}