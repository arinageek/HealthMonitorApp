package com.example.healthmonitorapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DayDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDay(day: Day)

    @Query("SELECT * FROM table_days WHERE date = :date")
    suspend fun getDayByDate(date: String): List<Day>

    @Query("SELECT * FROM table_days")
    fun getAllDays(): Flow<List<Day>>
}