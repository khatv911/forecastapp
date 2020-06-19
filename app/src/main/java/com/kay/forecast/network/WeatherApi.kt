package com.kay.forecast.network

import com.kay.forecast.BASE_URL
import com.kay.forecast.network.response.ForecastResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {


    @GET("/data/2.5/forecast/daily")
    suspend fun getForecast(
        @Query("appid") appid: String,
        @Query("q") query: String,
        @Query("cnt") count: Int,
        @Query("units") units: String = "metric" // i want Celsius

    ): ForecastResponse


    companion object {
        fun create(): WeatherApi {
            val logger =
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(WeatherApi::class.java)
        }
    }
}