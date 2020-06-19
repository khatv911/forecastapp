package com.kay.forecast.repository

import com.kay.forecast.NUM_OF_FORECASTS
import com.kay.forecast.network.WeatherApi
import com.kay.forecast.network.response.ForecastResponse
import com.kay.forecast.persistence.CacheDataSource
import com.kay.forecast.persistence.db.Forecast
import com.kay.forecast.persistence.query.QueryCache
import com.kay.forecast.persistence.query.QueryInfo
import retrofit2.HttpException
import timber.log.Timber

interface WeatherRepo {
    suspend fun getWeather(
        appId: String,
        query: String = "saigon",
        count: Int = NUM_OF_FORECASTS
    ): ForecastsWrapper
}

class WeatherRepoImpl(
    private val api: WeatherApi,
    private val queryCache: QueryCache,
    private val cacheDataSource: CacheDataSource
) : WeatherRepo {
    override suspend fun getWeather(appId: String, query: String, count: Int): ForecastsWrapper {
        return try {
            val queryInfo = queryCache.get(query)
            when (queryInfo.state) {
                QueryCache.CacheStatus.CITY_NOT_FOUND -> {
                    Timber.tag(TAG).e("q:$query City not found")
                    throw CityNotFound()
                }
                QueryCache.CacheStatus.EXPIRED_QUERY, QueryCache.CacheStatus.QUERY_NOT_FOUND -> {
                    Timber.tag(TAG).d("q:$query needs fresh search")
                    val response = api.getForecast(appId, query, count)
                    val (list, info) = convertNetworkResponse(query, response)
                    cacheDataSource.save(list)
                    val updatedInfo = info.copy(state = QueryCache.CacheStatus.QUERY_FOUND)
                    queryCache.put(updatedInfo)
                    queryFromCache(updatedInfo)
                }
                QueryCache.CacheStatus.QUERY_FOUND -> {
                    Timber.tag(TAG).d("q:$query found in cache")
                    queryFromCache(queryInfo)
                }
            }

        } catch (e: Throwable) {
            val customEx = filterNetworkException(e)
            if (customEx is CityNotFound) {
                Timber.tag(TAG).e("q:$query City not found")
                queryCache.put(QueryInfo(query, QueryCache.CacheStatus.CITY_NOT_FOUND))
            }
            throw customEx
        }
    }


    private suspend fun queryFromCache(queryInfo: QueryInfo) =
        ForecastsWrapper(
            cacheDataSource.fetch(
                queryInfo.cityId,
                queryInfo.minDate
            )
        )

    private fun convertNetworkResponse(
        query: String,
        response: ForecastResponse
    ): Pair<List<Forecast>, QueryInfo> {
        val result = mutableListOf<Forecast>()
        val cityId = response.city?.id ?: 0L
        response.weatherList?.forEach {
            result.add(
                Forecast(
                    cityId,
                    it.dt?.times(1000) ?: 0L, // epoch to millisecond
                    it.temp?.day, //avg temp
                    it.pressure,
                    it.humidity,
                    it.weather?.firstOrNull()?.description
                )
            )
        }
        val minDate = result.minBy { it.date ?: 0L }?.date
        val queryInfo =
            QueryInfo(query, QueryCache.CacheStatus.QUERY_NOT_FOUND, cityId, minDate ?: 0L)
        return Pair(result, queryInfo)
    }


    private fun filterNetworkException(e: Throwable): Throwable =
        when (e) {
            is HttpException -> {
                when {
                    e.code() == 401 -> UnAuthorized()
                    e.code() == 404 -> CityNotFound()
                    else -> e
                }
            }
            else -> e
        }

    companion object {
        const val TAG = "WeatherRepo"
    }

}