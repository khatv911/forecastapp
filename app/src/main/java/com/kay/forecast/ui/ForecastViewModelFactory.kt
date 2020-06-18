package com.kay.forecast.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kay.forecast.DependenciesContainer
import kotlinx.coroutines.Dispatchers

class ForecastViewModelFactory(private val dependenciesContainer: DependenciesContainer) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ForecastViewModel::class.java)) {
            val repo = dependenciesContainer.getTilesApi()
            ForecastViewModel(repo, Dispatchers.IO) as T
        } else {
            throw IllegalArgumentException("ViewModel Not Found")
        }
    }

}