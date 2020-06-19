package com.kay.forecast.persistence.db

import androidx.room.*

@Dao
interface ForecastDao {

    @Query("select * from tbl_forecast where cityId=:cityId and date >= :minDate limit :limit")
     fun loadAllForecasts(cityId: Long, minDate: Long, limit: Int) : List<Forecast>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertForecasts(forecasts: List<Forecast>)

    @Query("delete from tbl_forecast")
     fun deleteAll()
}