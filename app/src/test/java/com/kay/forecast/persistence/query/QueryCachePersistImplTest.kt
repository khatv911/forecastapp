package com.kay.forecast.persistence.query

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kay.forecast.App
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28], application = App::class)
class QueryCachePersistImplTest {

    private lateinit var app: App

    private lateinit var persistImpl: QueryCachePersistImpl

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext<App>()
        persistImpl = QueryCachePersistImpl(app)
    }

    @After
    fun tearDown() {
        persistImpl.clearForTesting()
    }


    @Test
    fun `what you persist is what you get`() {
        persistImpl.clearForTesting()
        assertTrue(persistImpl.getAll().isEmpty())
        val info1 = QueryInfo(
            "sai",
            QueryCache.CacheStatus.CITY_NOT_FOUND,
            0L,
            0L
        )
        persistImpl.persist(info1)
        val info2 = QueryInfo(
            "saigon",
            QueryCache.CacheStatus.QUERY_FOUND,
            1L,
            System.currentTimeMillis() - 1000
        )
        persistImpl.persist(info2)

        val retrievedMap = persistImpl.getAll()
        assertTrue(retrievedMap.size == 2)
        assertEquals(retrievedMap["sai"], info1)
        assertEquals(retrievedMap["saigon"], info2)

    }
}