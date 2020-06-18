package com.kay.forecast.persistence.entities

import androidx.recyclerview.widget.DiffUtil


data class Forecast(

    val cityId: Long? = null,

    val date: Long? = null,

    val avgTemp: Double? = null,

    val pressure: Int? = null,

    val humidity: Int? = null,

    val desc: String? = null
) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Forecast>() {
            override fun areItemsTheSame(oldItem: Forecast, newItem: Forecast): Boolean {
                return oldItem.cityId?.equals(newItem.cityId) ?: false
            }

            override fun areContentsTheSame(oldItem: Forecast, newItem: Forecast): Boolean {
                return oldItem == newItem
            }
        }
    }
}