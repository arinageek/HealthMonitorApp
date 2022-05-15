package com.example.healthmonitorapp.ui.Weather

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.healthmonitorapp.R
import com.example.healthmonitorapp.databinding.FragmentWeatherBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class WeatherFragment : Fragment(R.layout.fragment_weather) {

    private val viewModel by viewModels<WeatherViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentWeatherBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.event.collect { event ->
                when (event) {
                    is WeatherViewModel.WeatherEvent.showNoInternetConnectionMessage -> {
                        binding.textViewNoInternet.isVisible = true
                    }
                    is WeatherViewModel.WeatherEvent.removeNoInternetConnectionMessage -> {
                        binding.textViewNoInternet.isVisible = false
                    }
                }
            }
        }

        viewModel.weatherResponse.observe(viewLifecycleOwner) { weatherResponse ->
            weatherResponse?.let {
                binding.apply {
                    textViewCity.text = weatherResponse.name
                    textViewDegrees.text = weatherResponse.main.temp.toString() + "°C"
                    textViewDescription.text = weatherResponse.weather.description
                    textViewHumidity.text = "Humidity: " + weatherResponse.main.humidity.toString() + "%"
                    textViewTempMax.text = "Maximum temperature: " + weatherResponse.main.temp_max.toString() + "°C"
                    textViewTempMin.text = "Minimum temperature: " + weatherResponse.main.temp_min.toString() + "°C"
                    textViewPressure.text = "Pressure: " + weatherResponse.main.pressure.toString() + "hPa"
                    Glide.with(this@WeatherFragment)
                        .load("https://openweathermap.org/img/wn/" + weatherResponse.icon + "@2x.png")
                        .into(imageViewIcon)
                }
            }
        }
    }
}