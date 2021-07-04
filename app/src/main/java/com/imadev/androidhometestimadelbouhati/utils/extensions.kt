package com.imadev.androidhometestimadelbouhati.utils

import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource


fun GeoJsonSource.drawLayer(featureList: MutableList<Feature>){
    this.setGeoJson(FeatureCollection.fromFeatures(featureList))
}

fun View.snackbar(message:String){
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_LONG)
    snackbar.show()
}