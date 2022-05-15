package com.example.healthmonitorapp.ui.Weather

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.*
import com.example.healthmonitorapp.HealthApplication
import com.example.healthmonitorapp.api.models.WeatherResponse
import com.example.healthmonitorapp.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    app: Application,
    private val repository: HealthRepository
) : AndroidViewModel(app) {

    private val TAG = "WeatherViewModel"
    private val eventChannel = Channel<WeatherEvent>()
    val event = eventChannel.receiveAsFlow()

    init {
        if (isConnectedToInternet()) {
            viewModelScope.launch { eventChannel.send(WeatherEvent.removeNoInternetConnectionMessage) }
            getWeather()
        } else {
            viewModelScope.launch { eventChannel.send(WeatherEvent.showNoInternetConnectionMessage) }
        }
    }

    private val _weatherResponse: MutableLiveData<WeatherResponse> = MutableLiveData()
    val weatherResponse: LiveData<WeatherResponse>
        get() = _weatherResponse

    fun getWeather() = viewModelScope.launch {
        val response = repository.getWeather()
        if (response.isSuccessful) {
            response.body()?.let {
                _weatherResponse.postValue(it)
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
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            cm.activeNetworkInfo?.run {
                return when (type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

    sealed class WeatherEvent {
        object showNoInternetConnectionMessage: WeatherEvent()
        object removeNoInternetConnectionMessage: WeatherEvent()
    }
}