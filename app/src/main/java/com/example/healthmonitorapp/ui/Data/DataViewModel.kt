package com.example.healthmonitorapp.ui.Data

import androidx.lifecycle.*
import com.example.healthmonitorapp.database.Day
import com.example.healthmonitorapp.repository.HealthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DataViewModel @Inject constructor(
    private val repository: HealthRepository
) : ViewModel() {

    val days: LiveData<List<Day>>
        get() = repository.getAllDays().asLiveData()

}