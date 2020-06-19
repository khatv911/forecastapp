package com.kay.forecast.persistence.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.kay.forecast.App
import com.kay.forecast.fakeForeCastWrapper
import org.junit.After
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [28])
class ForecastCacheTest {

    private lateinit var db: AppDatabase

    private lateinit var forecastCache: ForecastCache

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<App>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        forecastCache = ForecastCache(db)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `test cache operations fetch and save`() {
        val fakeData = fakeForeCastWrapper().forecasts
        forecastCache.save(fakeData)

        val fetched = forecastCache.fetch(1L, 1592460590000)

        assertEquals(fakeData, fetched)
    }

}