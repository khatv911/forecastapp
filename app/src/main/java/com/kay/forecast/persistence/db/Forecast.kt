package com.kay.forecast.persistence.db

import androidx.recyclerview.widget.DiffUtil
import androidx.room.Entity


@Entity(
    tableName = "tbl_forecast",
    primaryKeys = ["cityId", "date"]
)
data class Forecast(

    val cityId: Long = 0L,

    val date: Long = 0L,

    val avgTemp: Double? = null,

    val pressure: Int? = null,

    val humidity: Int? = null,

    val desc: String? = null
) {
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Forecast>() {
            override fun areItemsTheSame(oldItem: Forecast, newItem: Forecast): Boolean {
                return oldItem.cityId == newItem.cityId
            }

            override fun areContentsTheSame(oldItem: Forecast, newItem: Forecast): Boolean {
                return oldItem == newItem
            }
        }
    }
}