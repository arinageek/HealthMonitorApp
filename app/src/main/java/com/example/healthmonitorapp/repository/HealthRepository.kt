package com.example.healthmonitorapp.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.healthmonitorapp.api.WeatherApi
import com.example.healthmonitorapp.api.models.WeatherResponse
import com.example.healthmonitorapp.database.Day
import com.example.healthmonitorapp.database.DayDao
import com.example.healthmonitorapp.ui.Settings.dataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import retrofit2.Response
import javax.inject.Inject

class HealthRepository @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val api: WeatherApi,
    private val dao: DayDao
) {

    suspend fun getWeather(): Response<WeatherResponse> {
        val city = stringPreferencesKey("city")
        val preferences = context.dataStore.data.first()
        //todo: implement chosen city logic - backend task
        return api.getWeather()
    }
    suspend fun getDayByDate(date: String) = dao.getDayByDate(date)
    suspend fun upsertDay(day: Day) = dao.upsertDay(day)
    fun getAllDays() = dao.getAllDays()
}