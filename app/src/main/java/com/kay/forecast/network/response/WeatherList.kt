package com.kay.forecast.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WeatherList {
    @SerializedName("dt")
    @Expose
    var dt: Long? = null

    @SerializedName("sunrise")
    @Expose
    var sunrise: Long? = null

    @SerializedName("sunset")
    @Expose
    var sunset: Long? = null

    @SerializedName("temp")
    @Expose
    var temp: Temp? = null

    @SerializedName("feels_like")
    @Expose
    var feelsLike: FeelsLike? = null

    @SerializedName("pressure")
    @Expose
    var pressure: Int? = null

    @SerializedName("humidity")
    @Expose
    var humidity: Int? = null

    @SerializedName("weather")
    @Expose
    var weather: List<Weather>? = null

    @SerializedName("speed")
    @Expose
    var speed: Double? = null

    @SerializedName("deg")
    @Expose
    var deg: Int? = null

    @SerializedName("clouds")
    @Expose
    var clouds: Int? = null

    @SerializedName("rain")
    @Expose
    var rain: Double? = null

}