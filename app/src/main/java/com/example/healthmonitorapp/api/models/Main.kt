package com.example.healthmonitorapp.api.models

data class Main(
    val humidity: Int,
    val pressure: Int,
    val temp: Int,
    val temp_max: Int,
    val temp_min: Int,
    val moon_phase: String,
)