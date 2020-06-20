package com.kay.forecast.network

import com.kay.forecast.API_URL
import com.kay.forecast.BASE_URL
import com.kay.forecast.PIN_SHA256
import com.kay.forecast.network.response.ForecastResponse
import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import timber.log.Timber

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
                HttpLoggingInterceptor(
                    object : HttpLoggingInterceptor.Logger {
                        override fun log(message: String) {
                            Timber.tag("OkHttp").d(message)
                        }
                    }
                ).apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }

            val client = OkHttpClient.Builder()
                .certificatePinner(
                    CertificatePinner.Builder().add(
                        API_URL, PIN_SHA256
                    ).build()
                )
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