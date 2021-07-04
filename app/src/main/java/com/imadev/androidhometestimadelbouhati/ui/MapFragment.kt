package com.imadev.androidhometestimadelbouhati.ui

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.imadev.androidhometestimadelbouhati.R
import com.imadev.androidhometestimadelbouhati.databinding.FragmentMapBinding
import com.imadev.androidhometestimadelbouhati.db.LocationDatabase
import com.imadev.androidhometestimadelbouhati.db.model.LocationEntity
import com.imadev.androidhometestimadelbouhati.repository.MapRepository
import com.imadev.androidhometestimadelbouhati.utils.Constants.Companion.CASABLANCA_DEFAULT_LOCATION
import com.imadev.androidhometestimadelbouhati.utils.drawLayer
import com.imadev.androidhometestimadelbouhati.utils.snackbar
import com.imadev.androidhometestimadelbouhati.viewmodel.MapViewModel
import com.imadev.androidhometestimadelbouhati.viewmodel.ViewModelFactory
import com.mapbox.geojson.*
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource


class MapFragment : BaseFragment<FragmentMapBinding>(), OnMapReadyCallback {
    private var mapView: MapView? = null
    private var mapBoxMap: MapboxMap? = null
    private var fillLayerPointList: MutableList<Point> = ArrayList()
    private var lineLayerPointList: MutableList<Point> = ArrayList()
    private var circleLayerFeatureList: MutableList<Feature> = ArrayList()
    private var listOfList: MutableList<MutableList<Point>>? = null
    private val finalFeatureList: MutableList<Feature> = ArrayList()
    private var circleSource: GeoJsonSource? = null
    private var fillSource: GeoJsonSource? = null
    private var lineSource: GeoJsonSource? = null
    private var firstPointOfPolygon: Point? = null
    private var builder: AlertDialog.Builder? = null
    private var dialog: AlertDialog? = null
    private lateinit var viewModel: MapViewModel

    companion object {
        private const val MAP_ANIMATION_DURATION = 4000
        private const val CIRCLE_SOURCE_ID = "circle-source-id"
        private const val FILL_SOURCE_ID = "fill-source-id"
        private const val LINE_SOURCE_ID = "line-source-id"
        private const val CIRCLE_LAYER_ID = "circle-layer-id"
        private const val FILL_LAYER_ID = "fill-layer-polygon-id"
        private const val LINE_LAYER_ID = "line-layer-id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Mapbox.getInstance(requireContext(), getString(R.string.mapbox_access_token))
        initViewModel()
    }

    private fun initViewModel() {
        val dao = LocationDatabase.getInstance(requireContext().applicationContext).locationDao
        val repository = MapRepository(dao)
        val factory = ViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory).get(MapViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapView = binding.mapBoxView
        mapView?.apply {
            onCreate(savedInstanceState)
            getMapAsync(this@MapFragment)
        }

        binding.clearBtn.setOnClickListener {
            clearEntireMap()
        }
        binding.saveBtn.setOnClickListener {

            if (lineLayerPointList.size > 2) {
                showPopUpDialog()
            } else {
                requireView().snackbar(getString(R.string.invalid_land_selection))
            }
        }
        viewModel.locationCoordinatesList.observe(viewLifecycleOwner, {
            Log.d("TAGG", "onViewCreated: $it")
            setSpinnerList(it as ArrayList<LocationEntity>)
        })
    }

    private fun setSpinnerList(list: ArrayList<LocationEntity>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            list
        )
        binding.spinner.apply {
            setAdapter(adapter)
            setOnItemClickListener { parent, view, position, id ->
                val item = parent.getItemAtPosition(position) as LocationEntity
                val points = item.locationCoordinatesList
                drawPolygon(points as MutableList<Point>)
                Log.d("TAGG", "setSpinnerList: ${points[0].latitude()}, ${points[0].longitude()}")

                zoomAndAnimateCamera(points[0].latitude(), points[0].longitude())
            }
        }
    }

    override fun onMapReady(map: MapboxMap) {
        mapBoxMap = map
        //SET MAP STYLE TO SATELLITE_STREETS
        mapBoxMap?.setStyle(Style.SATELLITE_STREETS) { style ->
            circleSource = initCircleSource(style);
            fillSource = initFillSource(style);
            lineSource = initLineSource(style);

            // Add layers to the map
            initCircleLayer(style)
            initLineLayer(style)
            initFillLayer(style)
            zoomAndAnimateCamera()
            listenToMapClick()
        }
    }

    private fun initCircleSource(loadedMapStyle: Style): GeoJsonSource? {
        val circleFeatureCollection = FeatureCollection.fromFeatures(arrayOf())
        val circleGeoJsonSource = GeoJsonSource(CIRCLE_SOURCE_ID, circleFeatureCollection)
        loadedMapStyle.addSource(circleGeoJsonSource)
        return circleGeoJsonSource
    }

    /**
     * Set up the CircleLayer for showing polygon click points
     */
    private fun initCircleLayer(loadedMapStyle: Style) {
        val circleLayer = CircleLayer(
            CIRCLE_LAYER_ID,
            CIRCLE_SOURCE_ID
        )
        circleLayer.setProperties(
            circleRadius(5.5f),
            circleColor(Color.parseColor("#d004d3"))
        )
        loadedMapStyle.addLayer(circleLayer)
    }


    private fun initFillSource(loadedMapStyle: Style): GeoJsonSource? {
        val fillFeatureCollection = FeatureCollection.fromFeatures(arrayOf())
        val fillGeoJsonSource = GeoJsonSource(FILL_SOURCE_ID, fillFeatureCollection)
        loadedMapStyle.addSource(fillGeoJsonSource)
        return fillGeoJsonSource
    }

    /**
     * Set up the FillLayer for showing the set boundaries' polygons
     */
    private fun initFillLayer(loadedMapStyle: Style) {
        val fillLayer = FillLayer(
            FILL_LAYER_ID,
            FILL_SOURCE_ID
        )
        fillLayer.setProperties(
            fillOpacity(.6f),
            fillColor(Color.GREEN)
        )
        loadedMapStyle.addLayerBelow(fillLayer, LINE_LAYER_ID)
    }

    /**
     * Set up the LineLayer source for showing map click points
     */
    private fun initLineSource(loadedMapStyle: Style): GeoJsonSource? {
        val lineFeatureCollection = FeatureCollection.fromFeatures(arrayOf())
        val lineGeoJsonSource = GeoJsonSource(LINE_SOURCE_ID, lineFeatureCollection)
        loadedMapStyle.addSource(lineGeoJsonSource)
        return lineGeoJsonSource
    }

    /**
     * Set up the LineLayer for showing the set boundaries' polygons
     */
    private fun initLineLayer(loadedMapStyle: Style) {
        val lineLayer = LineLayer(
            LINE_LAYER_ID,
            LINE_SOURCE_ID
        )
        lineLayer.setProperties(
            lineColor(Color.WHITE),
            lineWidth(2f),
            lineDasharray(arrayOf(2.0f, 1.0f))
        )
        loadedMapStyle.addLayerBelow(lineLayer, CIRCLE_LAYER_ID)
    }

    private fun listenToMapClick() {
        mapBoxMap?.let { map ->
            map.addOnMapClickListener { point ->
                // Use the map click location to create a Point object
                // Use the map click location to create a Point object
                val mapTargetPoint = Point.fromLngLat(
                    point.longitude,
                    point.latitude
                )

                // Make note of the first map click location so that it can be used to create a closed polygon later on

                // Make note of the first map click location so that it can be used to create a closed polygon later on
                if (circleLayerFeatureList.size == 0) {
                    firstPointOfPolygon = mapTargetPoint
                }

                // Add the click point to the circle layer and update the display of the circle layer data

                // Add the click point to the circle layer and update the display of the circle layer data
                circleLayerFeatureList.add(Feature.fromGeometry(mapTargetPoint))
                circleSource?.drawLayer(circleLayerFeatureList)

                // Add the click point to the line layer and update the display of the line layer data

                // Add the click point to the line layer and update the display of the line layer data

                when (circleLayerFeatureList.size) {
                    in 0..2 -> {
                        lineLayerPointList.add(mapTargetPoint)
                    }
                    3 -> {
                        lineLayerPointList.add(mapTargetPoint)
                        firstPointOfPolygon?.let { lineLayerPointList.add(it) }
                    }
                    else -> {
                        lineLayerPointList.removeAt(circleLayerFeatureList.size - 1)
                        lineLayerPointList.add(mapTargetPoint)
                        firstPointOfPolygon?.let { lineLayerPointList.add(it) }
                    }
                }

                if (lineSource != null) {
                    lineSource!!.drawLayer(
                        mutableListOf(
                            Feature.fromGeometry(
                                LineString.fromLngLats(lineLayerPointList)
                            )
                        )
                    )
                }

                // Add the click point to the fill layer and update the display of the fill layer data
                // Add the click point to the fill layer and update the display of the fill layer data

                when (circleLayerFeatureList.size) {
                    in 0..2 -> {
                        fillLayerPointList.add(mapTargetPoint)
                    }
                    3 -> {
                        fillLayerPointList.add(mapTargetPoint)
                        firstPointOfPolygon?.let { fillLayerPointList.add(it) }
                    }
                    else -> {
                        fillLayerPointList.removeAt(fillLayerPointList.size - 1)
                        fillLayerPointList.add(mapTargetPoint)
                        firstPointOfPolygon?.let { fillLayerPointList.add(it) }
                    }
                }

                drawPolygon()
                true
            }
        }
    }

    private fun drawPolygon() {
        finalFeatureList.clear()
        listOfList = ArrayList()
        (listOfList as ArrayList<MutableList<Point>>).add(fillLayerPointList)
        finalFeatureList.add(Feature.fromGeometry(Polygon.fromLngLats(listOfList as ArrayList<MutableList<Point>>)))
        if (fillSource != null) {
            fillSource!!.drawLayer(finalFeatureList)
        }
    }
    private fun drawPolygon(list:MutableList<Point>) {
        finalFeatureList.clear()
        listOfList = ArrayList()
        (listOfList as ArrayList<MutableList<Point>>).add(list)
        finalFeatureList.add(Feature.fromGeometry(Polygon.fromLngLats(listOfList as ArrayList<MutableList<Point>>)))
        if (fillSource != null) {
            fillSource!!.drawLayer(finalFeatureList)
        }
    }

    private fun clearEntireMap() {
        fillLayerPointList = ArrayList()
        circleLayerFeatureList = ArrayList()
        lineLayerPointList = ArrayList()
        if (circleSource != null) {
            circleSource!!.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
        }
        if (lineSource != null) {
            lineSource!!.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
        }
        if (fillSource != null) {
            fillSource!!.setGeoJson(FeatureCollection.fromFeatures(arrayOf()))
        }
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

    private fun zoomAndAnimateCamera(lat: Double, lang: Double) {
        val position = CameraPosition.Builder()
            .target(LatLng(lat, lang))
            .zoom(10.0)
            .tilt(20.0)
            .build()
        mapBoxMap?.animateCamera(
            CameraUpdateFactory.newCameraPosition(position),
            MAP_ANIMATION_DURATION
        )
    }

    private fun showPopUpDialog() {
        builder = AlertDialog.Builder(requireContext())
        val myView = layoutInflater.inflate(R.layout.location_name_pop_up, null)
        val editText = myView.findViewById<EditText>(R.id.nameEditText)
        builder?.setView(myView)
        dialog = builder?.create()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.show()
        myView?.findViewById<Button>(R.id.confirmBtn)?.setOnClickListener {
            if (editText.text.toString().isNotEmpty()) {
                viewModel.insertLocation(
                    LocationEntity(
                        0,
                        editText.text.toString(),
                        fillLayerPointList
                    )
                )
                dialog?.dismiss()
            } else {
                requireView().snackbar(getString(R.string.please_type_name))
            }
        }
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