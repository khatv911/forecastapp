package com.kay.forecast

import com.kay.forecast.network.response.*
import com.kay.forecast.persistence.entities.Forecast
import com.kay.forecast.persistence.entities.ForecastsWrapper
import com.kay.forecast.persistence.query.QueryCache.Companion.STALE_PERIOD
import com.kay.forecast.ui.ForecastAdapter

fun fakeForeCastWrapper(): ForecastsWrapper = ForecastsWrapper(
    listOf(
        Forecast(1L, 1592460591136, 30.0, 1005, 80, "It's gonna rain")
    )
)


fun fakeSuccessApiCall(): ForecastResponse = ForecastResponse().apply {
    city = City().apply { id=1L }
    weatherList = listOf(
        WeatherList().apply {
            dt = (System.currentTimeMillis() - STALE_PERIOD) / 1000 // millis to epoch
            temp = Temp().apply {
                day = 30.0
            }
            pressure  =1100
            humidity = 80
            weather = listOf(
                Weather().apply {
                    description = "Such a sunny day"
                }
            )

        },
        WeatherList().apply {
            dt = (System.currentTimeMillis()) / 1000 // millis to epoch
            temp = Temp().apply {
                day = 30.0
            }
            pressure  =1100
            humidity = 80
            weather = listOf(
                Weather().apply {
                    description = "Such a sunny day"
                }
            )

        }
    )

}