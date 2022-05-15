package com.example.healthmonitorapp.ui.Settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val eventChannel = Channel<Event>()
    val event = eventChannel.receiveAsFlow()

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    var calendar: Calendar? = null
    var storedAlarmTime: MutableLiveData<String?> = MutableLiveData("")
    var storedCity: MutableLiveData<String?> = MutableLiveData("")

    init {
        viewModelScope.launch {
            getCityFromPreferences()
            getAlarmTimeFromPreferences()
        }
    }

    fun saveCityIntoPreferences(city: String) = viewModelScope.launch {
        if (city.isBlank()) {
            eventChannel.send(Event.showCityEmptyNotification)
        } else {
            val cityPref = stringPreferencesKey("city")
            context.dataStore.edit { settings ->
                settings[cityPref] = city
            }
        }
    }

    suspend fun getCityFromPreferences() {
        val cityPref = stringPreferencesKey("city")
        val preferences = context.dataStore.data.first()
        storedCity.postValue(preferences[cityPref])
    }

    suspend fun getAlarmTimeFromPreferences() {
        val alarmTime = stringPreferencesKey("alarm_time")
        val preferences = context.dataStore.data.first()
        storedAlarmTime.postValue(preferences[alarmTime])
    }

    fun cancelAlarm() = viewModelScope.launch {
        alarmManager = context.getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)

        pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        alarmManager.cancel(pendingIntent)

        storedAlarmTime.postValue("No time chosen")
        val alarmTime = stringPreferencesKey("alarm_time")
        context.dataStore.edit { settings ->
            settings[alarmTime] = "No time chosen"
        }

        eventChannel.send(Event.showAlarmCancelledNotification)
    }

    fun setAlarm() = viewModelScope.launch {
        if (calendar != null) {
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)

            pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

            if (calendar!!.timeInMillis < System.currentTimeMillis()) {
                calendar!!.add(Calendar.HOUR_OF_DAY, 24)
            }

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP, calendar!!.timeInMillis,
                AlarmManager.INTERVAL_DAY, pendingIntent
            )

            val alarmTime = stringPreferencesKey("alarm_time")
            context.dataStore.edit { settings ->
                settings[alarmTime] = storedAlarmTime.value!!
            }
            eventChannel.send(Event.showAlarmSetNotification)
        } else {
            eventChannel.send(Event.showNoTimeChosenNotification)
        }
    }

    sealed class Event {
        object showCityEmptyNotification : Event()
        object showAlarmSetNotification : Event()
        object showAlarmCancelledNotification : Event()
        object showNoTimeChosenNotification : Event()
    }
}