package com.example.healthmonitorapp.api

import com.example.healthmonitorapp.api.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET

interface WeatherApi {

    @GET("current-weather")
    suspend fun getWeather(): Response<WeatherResponse>

}