package com.phellipesilva.currencyconverter.dependencyInjection

import android.content.Context
import android.content.SharedPreferences

import androidx.room.Room
import com.phellipesilva.currencyconverter.database.room.CurrencyDAO
import com.phellipesilva.currencyconverter.database.room.CurrencyDatabase
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class DatabaseModule {

    @Provides
    @Reusable
    fun providesCurrencyRateDAO(context: Context): CurrencyDAO =
        Room.databaseBuilder(context, CurrencyDatabase::class.java, "Currency Database")
            .build()
            .getCurrencyDAO()

    @Provides
    @Reusable
    fun providesSharedPrefs(context: Context): SharedPreferences = context.getSharedPreferences("SharedPrefs", Context.MODE_PRIVATE)
}