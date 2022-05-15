package com.example.healthmonitorapp.ui.Home

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthmonitorapp.HealthApplication
import com.example.healthmonitorapp.database.Day
import com.example.healthmonitorapp.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    app: Application,
    private val repository: HealthRepository
) : AndroidViewModel(app) {

    private var eventChannel = Channel<HomeEvent>()
    var event = eventChannel.receiveAsFlow()

    var healthState = MutableLiveData(HealthState.STATE_NONE)
    var comment = MutableLiveData("")

    init {
        checkIfFormAlreadySubmitted()
    }

    private fun createFormattedDate(): String {
        val fullDate = Date()
        val formatter = SimpleDateFormat("dd MM yyyy")
        return formatter.format(fullDate)
    }

    private fun checkIfFormAlreadySubmitted() {
        viewModelScope.launch {
            val date = createFormattedDate()
            val listOfDays = repository.getDayByDate(date)
            if (listOfDays.isNotEmpty()) {
                healthState.postValue(if (listOfDays[0].feeling == 0) HealthState.STATE_SICK else HealthState.STATE_HEALTHY)
                comment.postValue(listOfDays[0].comment)
            }
        }
    }

    fun onButtonSubmitClick() = viewModelScope.launch {
        val weatherResponse = repository.getWeather()
        if (weatherResponse.isSuccessful) {
            weatherResponse.body()?.let { response ->
                val day = with(response.main) {
                    Day(
                        date = createFormattedDate(),
                        feeling = if (healthState.value == HealthState.STATE_HEALTHY) 1 else 0,
                        comment = comment.value ?: "",
                        temp_min = temp_min,
                        temp_max = temp_max,
                        pressure = pressure,
                        humidity = humidity,
                        moon_phase = moon_phase
                    )
                }
                try {
                    repository.upsertDay(day)
                    eventChannel.send(HomeEvent.showFormSubmittedMessage)
                } catch (exc: Exception) {
                    eventChannel.send(HomeEvent.showFormNotSubmittedMessage)
                }
            }
        }
    }

    fun isConnectedToInternet(): Boolean {
        val cm = getApplication<HealthApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = cm.activeNetwork ?: return false
            val capabilities = cm.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            cm.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    sealed class HomeEvent {
        object showFormSubmittedMessage : HomeEvent()
        object showFormNotSubmittedMessage : HomeEvent()
    }
}

enum class HealthState { STATE_HEALTHY, STATE_SICK, STATE_NONE }