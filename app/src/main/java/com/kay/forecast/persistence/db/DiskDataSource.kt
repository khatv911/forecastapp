package com.kay.forecast.persistence.db

import com.kay.forecast.NUM_OF_FORECASTS
import com.kay.forecast.persistence.entities.Forecast

interface DiskDataSource {
    suspend fun fetch(
        cityId: Long,
        minDate: Long = 0L,
        limit: Int = NUM_OF_FORECASTS
    ): List<Forecast>

    suspend fun save(forecasts: List<Forecast>)

    companion object {
        fun inMem(): DiskDataSource = object : DiskDataSource {
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
        }
    }
}