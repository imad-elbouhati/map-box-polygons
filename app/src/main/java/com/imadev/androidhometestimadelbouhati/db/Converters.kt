package com.imadev.androidhometestimadelbouhati.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mapbox.geojson.Point


class Converters {
    @TypeConverter
    fun fromListToGson(value: List<Point>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromGsonToList(stringFeature: String): List<Point> {
       val type = object : TypeToken<List<Point>>() {}.type
        return Gson().fromJson(stringFeature, type)
    }

}