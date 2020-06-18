package com.kay.forecast.persistence.query

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kay.forecast.App
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28], application = App::class)
class QueryCacheImplTest {



    private lateinit var queryCache: QueryCache

    @MockK
    private lateinit var persistor: QueryCachePersistor


    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        queryCache = QueryCacheImpl(persistor)
        queryCache.initialize()

    }


    @Test
    fun `should get proper query info`() {

        // not found in cache
        val noCache = queryCache.get("no-key")
        assertEquals(QueryCache.CacheStatus.QUERY_NOT_FOUND, noCache.state)

        // query found in cache, but city not found
        val cityNotFound = QueryInfo(
            "saig",
            QueryCache.CacheStatus.CITY_NOT_FOUND,
            0L,
            0L
        )
        queryCache.put(cityNotFound)
        assertEquals(QueryCache.CacheStatus.CITY_NOT_FOUND, queryCache.get("saig").state)

        // query found in cache, but expired
        val expiredQuery = QueryInfo(
            "sai",
            QueryCache.CacheStatus.QUERY_FOUND,
            1L,
            System.currentTimeMillis() - QueryCache.STALE_PERIOD - 1000
        )
        queryCache.put(expiredQuery)
        assertEquals(QueryCache.CacheStatus.EXPIRED_QUERY, queryCache.get("sai").state)

        // query found, not expired
        val goodQuery = QueryInfo(
            "saigon",
            QueryCache.CacheStatus.QUERY_FOUND,
            1L,
            System.currentTimeMillis() - 1000
        )
        queryCache.put(goodQuery)
        assertEquals(QueryCache.CacheStatus.QUERY_FOUND, queryCache.get("saigon").state)

        queryCache.persist()
        verify { persistor.persist(any()) }

    }


}