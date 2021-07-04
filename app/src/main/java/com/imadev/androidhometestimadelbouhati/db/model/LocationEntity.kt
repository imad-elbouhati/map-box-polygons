package com.imadev.androidhometestimadelbouhati.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.imadev.androidhometestimadelbouhati.utils.Constants.Companion.LOCATION_COORDINATES
import com.imadev.androidhometestimadelbouhati.utils.Constants.Companion.LOCATION_ID
import com.imadev.androidhometestimadelbouhati.utils.Constants.Companion.LOCATION_NAME
import com.imadev.androidhometestimadelbouhati.utils.Constants.Companion.TABLE_NAME
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point


@Entity(tableName = TABLE_NAME)
data class LocationEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = LOCATION_ID)
    val id: Int,
    @ColumnInfo(name = LOCATION_NAME)
    val name: String,
    @ColumnInfo(name = LOCATION_COORDINATES)
    val locationCoordinatesList: List<Point>
){
    override fun toString(): String {
        return name
    }
}

