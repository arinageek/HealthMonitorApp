package com.example.healthmonitorapp.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "table_days")
data class Day(
    @PrimaryKey
    val date: String,
    val feeling: Int, // Healthy = 1, Sick = 0
    val comment: String = "",
    val temp_min: Int,
    val temp_max: Int,
    val pressure: Int,
    val humidity: Int,
    val moon_phase: String,
)
