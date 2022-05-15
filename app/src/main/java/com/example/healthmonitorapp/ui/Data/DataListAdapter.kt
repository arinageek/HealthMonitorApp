package com.example.healthmonitorapp.ui.Data

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmonitorapp.database.Day
import com.example.healthmonitorapp.databinding.RecyclerViewDataItemBinding

class DataListAdapter(private val dataSet: List<Day>) :
    RecyclerView.Adapter<DataListAdapter.ViewHolder>() {

    class ViewHolder(private val binding: RecyclerViewDataItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(day: Day) {
            binding.apply {
                rootLayout.setBackgroundColor(
                    if (day.feeling == 1) Color.parseColor("#99ffa0")
                    else Color.parseColor("#fcaeae")
                )
                textViewDate.text = day.date
                textViewComment.text = "Comment: " + day.comment
                textViewHumidity.text = "Humidity: " + day.humidity.toString() + "%"
                textViewTempMin.text = "Minimum temperature: " + day.temp_min.toString() + "°C"
                textViewTempMax.text = "Maximum temperature: " + day.temp_max.toString() + "°C"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerViewDataItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int = dataSet.size

}