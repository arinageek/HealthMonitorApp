package com.example.healthmonitorapp.api.models

data class WeatherResponse(
    val weather: Weather,
    val main: Main,
    val name: String,
    val icon: String,
)