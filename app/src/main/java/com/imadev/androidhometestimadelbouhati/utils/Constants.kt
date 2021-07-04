package com.imadev.androidhometestimadelbouhati.utils

import com.mapbox.mapboxsdk.geometry.LatLng

class Constants {

    companion object {
        val CASABLANCA_DEFAULT_LOCATION = LatLng(33.56999904831415, -7.590082404718629)
        const val TABLE_NAME = "location_table"
        const val LOCATION_ID = "id"
        const val LOCATION_NAME = "name"
        const val LOCATION_COORDINATES ="coordinates"
        const val LOCATION_DATABASE = "location_database"
    }
}