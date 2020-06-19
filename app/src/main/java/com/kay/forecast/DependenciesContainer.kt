package com.kay.forecast

import android.app.Application
import com.google.gson.Gson
import com.kay.forecast.network.WeatherApi
import com.kay.forecast.persistence.db.DataSource
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

    private val repo: WeatherRepo by lazy {
        WeatherRepoImpl(WeatherApi.create(), queryCache, DataSource.inMem())
    }
    fun getTilesApi() = repo
}