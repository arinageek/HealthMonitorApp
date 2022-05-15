package com.example.healthmonitorapp.di

import android.content.Context
import androidx.room.Room
import com.example.healthmonitorapp.api.WeatherApi
import com.example.healthmonitorapp.database.DayDao
import com.example.healthmonitorapp.database.DayDatabase
import com.example.healthmonitorapp.utils.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofitInstance(): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideWeatherApi(retrofit: Retrofit): WeatherApi =
        retrofit.create(WeatherApi::class.java)

    @Provides
    @Singleton
    fun provideDayDatabase(
        @ApplicationContext context: Context
    ): DayDatabase = Room.databaseBuilder(
            context,
            DayDatabase::class.java,
            "day_db"
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideDayDao(db: DayDatabase): DayDao = db.getDao()

}
