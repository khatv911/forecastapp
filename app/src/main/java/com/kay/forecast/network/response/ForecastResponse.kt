package com.kay.forecast.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ForecastResponse {
    @SerializedName("city")
    @Expose
    var city: City? = null

    @SerializedName("cod")
    @Expose
    var cod: String? = null

    @SerializedName("message")
    @Expose
    var message: Double? = null

    @SerializedName("cnt")
    @Expose
    var cnt: Int? = null

    @SerializedName("list")
    @Expose
    var weatherList: List<WeatherList>? = null

}