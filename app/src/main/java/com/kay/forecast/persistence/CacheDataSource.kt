package com.kay.forecast.persistence

import androidx.annotation.VisibleForTesting
import com.kay.forecast.NUM_OF_FORECASTS
import com.kay.forecast.persistence.db.AppDatabase
import com.kay.forecast.persistence.db.Forecast
import com.kay.forecast.persistence.db.ForecastCache

interface CacheDataSource {
    fun fetch(
        cityId: Long,
        minDate: Long = 0L,
        limit: Int = NUM_OF_FORECASTS
    ): List<Forecast>

    fun save(forecasts: List<Forecast>)

    fun clearAll()

    companion object {
        /**
         * store in memory, use for testing only
         */
        @VisibleForTesting
        fun inMem(): CacheDataSource = object :
            CacheDataSource {
            private val mutableList = mutableListOf<Forecast>()
            override fun fetch(cityId: Long, minDate: Long, limit: Int): List<Forecast> {
                return mutableList.filter {
                    it.cityId == cityId && it.date >= minDate
                }.take(limit)
            }

            override fun save(forecasts: List<Forecast>) {
                mutableList.addAll(forecasts)
            }

            override fun clearAll() {
                mutableList.clear()
            }
        }

        /**
         * Use Room as backed storage
         */
        fun inDb(appDb: AppDatabase): CacheDataSource = ForecastCache(appDb)
    }


}