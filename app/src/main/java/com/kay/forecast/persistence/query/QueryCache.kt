package com.kay.forecast.persistence.query

import androidx.collection.LruCache
import com.google.gson.annotations.SerializedName
import com.kay.forecast.persistence.query.QueryCache.CacheStatus.CITY_NOT_FOUND
import com.kay.forecast.persistence.query.QueryCache.CacheStatus.EXPIRED_QUERY
import com.kay.forecast.persistence.query.QueryCache.CacheStatus.QUERY_FOUND
import com.kay.forecast.persistence.query.QueryCache.CacheStatus.QUERY_NOT_FOUND
import com.kay.forecast.persistence.query.QueryCache.Companion.STALE_PERIOD
import java.util.concurrent.Semaphore


data class QueryInfo(
    @SerializedName("query")
    val query: String,
    @SerializedName("state")
    val state: QueryCache.CacheStatus = QueryCache.CacheStatus.QUERY_NOT_FOUND,
    @SerializedName("cityId")
    val cityId: Long = Long.MIN_VALUE,
    @SerializedName("minDate")
    val minDate: Long = 0L
)


interface QueryCache {
    /**
     *
     */
    fun initialize()

    /**
     * check if query is stored in cache
     */
    fun get(key: String): QueryInfo

    /**
     * Put a query in cache
     */
    fun put(value: QueryInfo)

    /**
     * Persist this cache to disk
     */
    fun persist()

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
    private val lruCache: LruCache<String, QueryInfo> = LruCache(50)
    private val semaphore = Semaphore(1)

    override fun initialize() {
        persistHelper.get().forEach { (t, u) -> lruCache.put(t, u) }
    }

    override fun get(key: String): QueryInfo {
        try {
            semaphore.acquire()
            val queryInfo = lruCache[key]
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
        } finally {
            semaphore.release()
        }

    }

    override fun put(value: QueryInfo) {
        try {
            semaphore.acquire()
            lruCache.put(value.query, value)
        } finally {
            semaphore.release()
        }

    }

    override fun persist() {
        persistHelper.persist(lruCache.snapshot())
        lruCache.evictAll()
    }
}