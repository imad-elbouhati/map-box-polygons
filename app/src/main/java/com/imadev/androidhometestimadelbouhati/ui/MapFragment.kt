package com.imadev.androidhometestimadelbouhati.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imadev.androidhometestimadelbouhati.R
import com.imadev.androidhometestimadelbouhati.databinding.FragmentMapBinding
import com.imadev.androidhometestimadelbouhati.utils.Constants.Companion.CASABLANCA_DEFAULT_LOCATION
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style


class MapFragment : BaseFragment<FragmentMapBinding>(), OnMapReadyCallback {
    private var mapView: MapView? = null
    private val POINTS = mutableListOf<List<Point>>()
    private val OUTER_POINTS = mutableListOf<Point>()
    private var mapBoxMap: MapboxMap? = null

    companion object {
        const val MAP_ANIMATION_DURATION = 4000
    }

    init {
        OUTER_POINTS.add(Point.fromLngLat(-122.685699, 45.522585))
        OUTER_POINTS.add(Point.fromLngLat(-122.708873, 45.534611))
        OUTER_POINTS.add(Point.fromLngLat(-122.678833, 45.530883))
        OUTER_POINTS.add(Point.fromLngLat(-122.667503, 45.547115))
        OUTER_POINTS.add(Point.fromLngLat(-122.660121, 45.530643))
        OUTER_POINTS.add(Point.fromLngLat(-122.636260, 45.533529))
        OUTER_POINTS.add(Point.fromLngLat(-122.659091, 45.521743))
        OUTER_POINTS.add(Point.fromLngLat(-122.648792, 45.510677))
        OUTER_POINTS.add(Point.fromLngLat(-122.664070, 45.515008))
        OUTER_POINTS.add(Point.fromLngLat(-122.669048, 45.502496))
        OUTER_POINTS.add(Point.fromLngLat(-122.678489, 45.515369))
        OUTER_POINTS.add(Point.fromLngLat(-122.702007, 45.506346))
        OUTER_POINTS.add(Point.fromLngLat(-122.685699, 45.522585))
        POINTS.add(OUTER_POINTS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapView = binding.mapBoxView

        mapView?.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MapFragment)
        }
    }

    override fun onMapReady(map: MapboxMap) {
        mapBoxMap = map
        //SET MAP STYLE TO SATELLITE_STREETS
        mapBoxMap?.setStyle(Style.SATELLITE_STREETS)

        zoomAndAnimateCamera()
    }

    private fun zoomAndAnimateCamera() {
        val position = CameraPosition.Builder()
            .target(CASABLANCA_DEFAULT_LOCATION)
            .zoom(10.0)
            .tilt(20.0)
            .build()
        mapBoxMap?.animateCamera(
            CameraUpdateFactory.newCameraPosition(position),
            MAP_ANIMATION_DURATION
        )
    }

    override fun createViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentMapBinding = FragmentMapBinding.inflate(inflater, container, false)

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }


}