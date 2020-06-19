package com.kay.forecast.ui

import androidx.lifecycle.*
import com.kay.forecast.APP_ID
import com.kay.forecast.MIN_QUERY_LENGTH
import com.kay.forecast.persistence.entities.ForecastsWrapper
import com.kay.forecast.repository.InsufficientSearch
import com.kay.forecast.repository.WeatherRepo
import kotlinx.coroutines.*

class ForecastViewModel constructor(
    private val repo: WeatherRepo,
    private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _uiStateLiveData = MutableLiveData<UiState<ForecastsWrapper>>()

    val uiStateLiveData: LiveData<UiState<ForecastsWrapper>>
        get() = _uiStateLiveData

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        _uiStateLiveData.value = UiState.Error(exception)
    }

    fun queryCity(city: String) {
        if (city.length < MIN_QUERY_LENGTH) {
            _uiStateLiveData.value = UiState.Error(InsufficientSearch())
        } else {
            _uiStateLiveData.value = UiState.Loading
            viewModelScope.launch(exceptionHandler) {
                val res = withContext(ioDispatcher) {
                    repo.getWeather(APP_ID, city)
                }
                _uiStateLiveData.value = UiState.Success(res)
            }
        }
    }
}