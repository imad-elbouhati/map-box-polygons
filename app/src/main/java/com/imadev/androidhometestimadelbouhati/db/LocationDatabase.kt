package com.imadev.androidhometestimadelbouhati.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.imadev.androidhometestimadelbouhati.db.model.LocationEntity
import com.imadev.androidhometestimadelbouhati.utils.Constants.Companion.LOCATION_DATABASE

@Database(entities = [LocationEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class LocationDatabase : RoomDatabase() {

    abstract val locationDao: LocationDao

    companion object {
        @Volatile
        private var INSTANCE: LocationDatabase? = null
        fun getInstance(context: Context): LocationDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LocationDatabase::class.java,
                        LOCATION_DATABASE
                    ).build()
                }
                return instance
            }
        }
    }

}