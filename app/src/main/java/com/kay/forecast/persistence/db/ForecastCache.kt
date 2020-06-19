package com.kay.forecast.persistence.db

import com.kay.forecast.persistence.CacheDataSource

class ForecastCache(appDb: AppDatabase) : CacheDataSource {
    private val dao = appDb.forecastDao()
    override fun fetch(cityId: Long, minDate: Long, limit: Int): List<Forecast> =
        dao.loadAllForecasts(cityId, minDate, limit)

    override fun save(forecasts: List<Forecast>) = dao.insertForecasts(forecasts)

    override fun clearAll() = dao.deleteAll()
}