package com.kay.forecast

import android.app.Application
import androidx.room.Room
import com.kay.forecast.network.WeatherApi
import com.kay.forecast.persistence.db.AppDatabase
import com.kay.forecast.persistence.CacheDataSource
import com.kay.forecast.persistence.query.QueryCacheImpl
import com.kay.forecast.persistence.query.QueryCachePersistImpl
import com.kay.forecast.repository.WeatherRepo
import com.kay.forecast.repository.WeatherRepoImpl

/**
 * Just a simple singleton Service Locator.
 */
class DependenciesContainer(app: Application) {

    private val queryCachePersist = QueryCachePersistImpl(app)

    private val queryCache = QueryCacheImpl(queryCachePersist)

    private val db = Room.databaseBuilder(
        app,
        AppDatabase::class.java, "forecast.db"
    ).build()

    val repo: WeatherRepo by lazy {
        WeatherRepoImpl(WeatherApi.create(), queryCache, CacheDataSource.inDb(db))
    }

}