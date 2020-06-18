package com.kay.forecast

import com.kay.forecast.persistence.entities.Forecast
import com.kay.forecast.persistence.entities.ForecastsWrapper

fun fakeForeCastWrapper(): ForecastsWrapper = ForecastsWrapper(
    listOf(
        Forecast(0, 1592460591136, 30.0, 1005, 80, "It's gonna rain")
//        Forecast(1, 1592460591136, 30.0, 1005, 81, "It's gonna rain"),
//        Forecast(2, 1592460591136, 30.0, 1005, 82, "It's gonna rain")
    )
)