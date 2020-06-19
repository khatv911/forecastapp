package com.kay.forecast.repository

import com.kay.forecast.persistence.db.Forecast

data class ForecastsWrapper(val forecasts: List<Forecast>)