package com.kay.forecast.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kay.forecast.R
import com.kay.forecast.persistence.db.Forecast
import com.kay.forecast.utils.millisToDate
import kotlinx.android.synthetic.main.item_weather.view.*

class ForecastAdapter :
    ListAdapter<Forecast, ForecastAdapter.ForecastViewHolder>(
        Forecast.DIFF_CALLBACK
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        return ForecastViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_weather, parent, false)
        )
    }


    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ForecastViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(forecast: Forecast) = with(itemView) {
            tvDate.text = resources.getString(R.string.date, forecast.date.millisToDate())
            tvAvgTemp.text = resources.getString(R.string.avgTemp, forecast.avgTemp?.toInt())
            tvHumidity.text = resources.getString(R.string.humidity, forecast.humidity)
            tvPressure.text = resources.getString(R.string.pressure, forecast.pressure)
            tvDesc.text = resources.getString(R.string.description, forecast.desc ?: "N/A")
        }
    }
}