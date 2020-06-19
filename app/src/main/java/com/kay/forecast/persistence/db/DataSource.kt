package com.kay.forecast.persistence.db

import com.kay.forecast.NUM_OF_FORECASTS
import com.kay.forecast.persistence.entities.Forecast

interface DataSource {
    suspend fun fetch(
        cityId: Long,
        minDate: Long = 0L,
        limit: Int = NUM_OF_FORECASTS
    ): List<Forecast>

    suspend fun save(forecasts: List<Forecast>)

    suspend fun clearAll()

    companion object {
        fun inMem(): DataSource = object : DataSource {
            private val mutableList = mutableListOf<Forecast>()
            override suspend fun fetch(cityId: Long, minDate: Long, limit: Int): List<Forecast> {
                return mutableList.filter {
                    it.cityId?.equals(cityId) ?: false
                            && (it.date?.compareTo(minDate) ?: 0) >= 0
                }.take(limit)
            }

            override suspend fun save(forecasts: List<Forecast>) {
                mutableList.addAll(forecasts)
            }

            override suspend fun clearAll() {
                mutableList.clear()
            }
        }
    }
}