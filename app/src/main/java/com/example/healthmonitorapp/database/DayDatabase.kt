package com.example.healthmonitorapp.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Day::class], version = 2)
abstract class DayDatabase : RoomDatabase() {
    abstract fun getDao(): DayDao
}