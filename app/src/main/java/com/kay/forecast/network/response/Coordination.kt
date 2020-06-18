package com.kay.forecast.network.response

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Coordination {
    @SerializedName("lon")
    @Expose
    var lon: Double? = null

    @SerializedName("lat")
    @Expose
    var lat: Double? = null

}