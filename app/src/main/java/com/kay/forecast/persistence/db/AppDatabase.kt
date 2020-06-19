package com.kay.forecast.persistence.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Forecast::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun forecastDao(): ForecastDao
}