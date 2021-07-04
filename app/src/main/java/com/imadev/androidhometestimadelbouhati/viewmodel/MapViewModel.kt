package com.imadev.androidhometestimadelbouhati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.imadev.androidhometestimadelbouhati.db.model.LocationEntity
import com.imadev.androidhometestimadelbouhati.repository.MapRepository
import kotlinx.coroutines.launch

class MapViewModel(private val repository: MapRepository): ViewModel() {
    val locationCoordinatesList = repository.locationCoordinatesList

    fun insertLocation(location: LocationEntity) = viewModelScope.launch{
        repository.insert(location)
    }
}