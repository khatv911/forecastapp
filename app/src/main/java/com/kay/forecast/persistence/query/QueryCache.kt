package com.kay.forecast.persistence.query

import com.google.gson.annotations.SerializedName
import com.kay.forecast.persistence.query.QueryCache.CacheStatus.CITY_NOT_FOUND
import com.kay.forecast.persistence.query.QueryCache.CacheStatus.EXPIRED_QUERY
import com.kay.forecast.persistence.query.QueryCache.CacheStatus.QUERY_FOUND
import com.kay.forecast.persistence.query.QueryCache.CacheStatus.QUERY_NOT_FOUND
import com.kay.forecast.persistence.query.QueryCache.Companion.STALE_PERIOD
import java.util.concurrent.ConcurrentHashMap


data class QueryInfo(
    @SerializedName("query")
    val query: String,
    @SerializedName("state")
    val state: QueryCache.CacheStatus = QUERY_NOT_FOUND,
    @SerializedName("cityId")
    val cityId: Long = Long.MIN_VALUE,
    @SerializedName("minDate")
    val minDate: Long = 0L
)


interface QueryCache {
    /**
     * check if query is stored in cache
     */
    fun get(key: String): QueryInfo

    /**
     * Put a query in cache
     */
    fun put(info: QueryInfo)


    enum class CacheStatus {
        QUERY_NOT_FOUND, //fresh search
        CITY_NOT_FOUND, //404, query cached
        EXPIRED_QUERY,
        QUERY_FOUND
    }

    companion object {
        // use for query cache state
        const val STALE_PERIOD = 24 * 60 * 60 * 1000L // 1 day

    }
}


class QueryCacheImpl(
    private val persistHelper: QueryCachePersistor,
    private val stalePeriod: Long = STALE_PERIOD
) :
    QueryCache {
    private val memCache: ConcurrentHashMap<String, QueryInfo> = ConcurrentHashMap()

    init {
        persistHelper.getAll().forEach { (t, u) -> memCache.put(t, u) }
    }


    override fun get(key: String): QueryInfo {

        val queryInfo = memCache[key]
        return if (queryInfo == null) {
            QueryInfo(
                key,
                state = QUERY_NOT_FOUND
            )
        } else {
            when {
                queryInfo.state == CITY_NOT_FOUND -> queryInfo
                queryInfo.minDate + stalePeriod < System.currentTimeMillis() -> queryInfo.copy(
                    state = EXPIRED_QUERY
                )
                else -> queryInfo.copy(state = QUERY_FOUND)
            }
        }
    }

    override fun put(info: QueryInfo) {
        memCache[info.query] = info
        persistHelper.persist(info)
    }


}