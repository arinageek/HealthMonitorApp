package com.example.healthmonitorapp.ui.Settings

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.ALARM_SERVICE
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.healthmonitorapp.R
import com.example.healthmonitorapp.database.Day
import com.example.healthmonitorapp.databinding.FragmentSettingsBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private lateinit var picker: MaterialTimePicker
    private val viewModel by viewModels<SettingsViewModel>()

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        createNotificationChannel()

        binding.etCity.setText(viewModel.storedCity.value ?: "")

        viewModel.storedAlarmTime.observe(viewLifecycleOwner) {
            binding.tvSelectedTime.text = if (it.isNullOrEmpty()) "No chosen time" else it
        }
        viewModel.storedCity.observe(viewLifecycleOwner) {
            binding.etCity.setText(if (it.isNullOrEmpty()) "" else it)
        }
        binding.btnSaveCity.setOnClickListener { viewModel.saveCityIntoPreferences(binding.etCity.text.toString()) }
        binding.btnSelectTime.setOnClickListener { showTimePicker() }
        binding.btnSetAlarm.setOnClickListener { viewModel.setAlarm() }
        binding.btnCancelAlarm.setOnClickListener { viewModel.cancelAlarm() }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.event.collect { event ->
                when (event) {
                    is SettingsViewModel.Event.showCityEmptyNotification -> {
                        Snackbar.make(
                            binding.root,
                            "City field can't be empty",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is SettingsViewModel.Event.showAlarmSetNotification -> {
                        Snackbar.make(
                            binding.root,
                            "Alarm has been successfuly set",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is SettingsViewModel.Event.showAlarmCancelledNotification -> {
                        Snackbar.make(
                            binding.root,
                            "Alarm has been cancelled",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                    is SettingsViewModel.Event.showNoTimeChosenNotification -> {
                        Snackbar.make(
                            binding.root,
                            "Please set the time before creating an alarm",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        return binding.root
    }

    private fun showTimePicker() {

        picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("Select Alarm Time")
            .build()

        picker.show(childFragmentManager, "health_monitor")

        picker.addOnPositiveButtonClickListener {

            val hour = if (picker.hour < 10) "0" + picker.hour.toString()
            else picker.hour.toString()

            val minute = if (picker.minute < 10) "0" + picker.minute.toString()
            else picker.minute.toString()

            binding.tvSelectedTime.text = hour + ":" + minute
            viewModel.storedAlarmTime.postValue(hour + ":" + minute)

            viewModel.calendar = Calendar.getInstance().apply {
                this[Calendar.HOUR_OF_DAY] = picker.hour
                this[Calendar.MINUTE] = picker.minute
                this[Calendar.SECOND] = 0
                this[Calendar.MILLISECOND] = 0
            }

        }

    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "HealthMonitor"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("HealthMonitor", name, importance)
            val notificationManager = NotificationManagerCompat.from(requireActivity())
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}