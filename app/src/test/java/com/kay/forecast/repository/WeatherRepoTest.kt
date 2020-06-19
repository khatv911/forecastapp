package com.kay.forecast.repository

import com.kay.forecast.APP_ID
import com.kay.forecast.fakeForeCastWrapper
import com.kay.forecast.fakeSuccessApiCall
import com.kay.forecast.network.WeatherApi
import com.kay.forecast.persistence.CacheDataSource
import com.kay.forecast.persistence.query.QueryCache
import com.kay.forecast.persistence.query.QueryInfo
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
class WeatherRepoTest {

    private lateinit var repoImpl: WeatherRepo

    @MockK
    private lateinit var weatherApi: WeatherApi

    @MockK
    private lateinit var queryCache: QueryCache

    private val dataSource = CacheDataSource.inMem()

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        runBlocking { dataSource.clearAll() }
        repoImpl = WeatherRepoImpl(
            weatherApi,
            queryCache,
            dataSource
        )
    }

    @Test(expected = CityNotFound::class)
    fun `given cityNotFound in cache, when call getWeather() should throw CityNotFound immediately`() =
        runBlockingTest {
            val query = "sgn"
            every { queryCache.get(query) } returns QueryInfo(
                query,
                QueryCache.CacheStatus.CITY_NOT_FOUND
            )
            repoImpl.getWeather(APP_ID, query)
        }

    @Test(expected = CityNotFound::class)
    fun `given query not in cache, when call getWeather() and network return notfound should throw CityNotFound immediately`() =
        runBlockingTest {
            every { queryCache.get(any()) } returns QueryInfo(
                "notInCache",
                QueryCache.CacheStatus.QUERY_NOT_FOUND
            )
            coEvery { weatherApi.getForecast(any(), any(), any(), any()) } throws HttpException(
                Response.error<Any>(404, ByteArray(0).toResponseBody())
            )
            repoImpl.getWeather(APP_ID, "notInCache")
        }

    @Test
    fun `given query expired, and api responses success, should give proper data`() =
        runBlockingTest {
            every { queryCache.get(any()) } returns QueryInfo(
                "saigon",
                QueryCache.CacheStatus.EXPIRED_QUERY
            )

            val fakeData = fakeSuccessApiCall()
            coEvery { weatherApi.getForecast(any(), any(), any(), any()) } coAnswers { fakeData }

            val result = repoImpl.getWeather(APP_ID, "saigon")

            assertEquals(result.forecasts.size, fakeData.weatherList?.size)
            assertEquals(result.forecasts[0].cityId, fakeData.city?.id)

        }

    @Test
    fun `given query is still valid, should fetch from datasource only`() = runBlockingTest {
        // put in cache first
        val fakeData = fakeForeCastWrapper().forecasts
        dataSource.save(fakeData)
        every { queryCache.get(any()) } returns QueryInfo(
            "saigon",
            QueryCache.CacheStatus.QUERY_FOUND,
            cityId = 1L,
            minDate = 1592460591136
        )

        val result = repoImpl.getWeather(APP_ID, "saigon")
        // we skip the network
        verify { weatherApi wasNot Called }

        assertEquals(result.forecasts, fakeData)

    }

}