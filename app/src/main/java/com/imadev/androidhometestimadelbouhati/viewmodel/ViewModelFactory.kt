package com.imadev.androidhometestimadelbouhati.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.imadev.androidhometestimadelbouhati.repository.MapRepository
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val repository: MapRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(MapViewModel::class.java)){
            return MapViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel Not Found")
    }
}